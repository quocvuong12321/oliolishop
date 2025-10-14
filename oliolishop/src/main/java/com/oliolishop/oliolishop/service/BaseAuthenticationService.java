package com.oliolishop.oliolishop.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.oliolishop.oliolishop.constant.TokenType;
import com.oliolishop.oliolishop.dto.Token.AccessTokenResponse;
import com.oliolishop.oliolishop.dto.Token.RefreshTokenRequest;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateRequest;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateResponse;
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseAuthenticationService<T> implements AuthenticationService {
    protected final RefreshTokenService refreshTokenService;
    protected final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    @NonFinal
    @Value("${jwt.signerKey}")
    protected  String SIGNER_KEY;
    protected static final long TIME_ACCESS = 15;       // 15 phút
    protected static final long TIME_REFRESH = 60 * 24; // 24 giờ


    protected abstract T findUserByUsername(String username);
    protected abstract String getUsername(T user);
    protected abstract String getPassword(T user);
    protected abstract String buildScope(T user);

    @Override
    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        T user = findUserByUsername(request.getUsername());
        if (user == null) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_EXISTED);
        }

        if (!passwordEncoder.matches(request.getPassword(), getPassword(user))) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        String accessToken = generateToken(user, TIME_ACCESS, TokenType.ACCESSTYPE);
        String refreshToken = generateToken(user, TIME_REFRESH, TokenType.REFRESHTYPE);

        refreshTokenService.storeRefreshToken(getUsername(user), refreshToken, (int)TIME_REFRESH);

        return AuthenticateResponse.builder()
                .authenticated(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    protected String generateToken(T user, long expiryMinutes, String tokenType) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(getUsername(user))
                .issuer("oliolishop")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES)))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .claim("type", tokenType);
        // 👉 Cho phép class con thêm claim tùy chỉnh ở đây
        addIdClaims(claimsBuilder,user);
        JWTClaimsSet claims = claimsBuilder.build();
        try {
            JWSObject jwsObject = new JWSObject(header, new Payload(claims.toJSONObject()));
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    // 👇 Hook method để class con override nếu muốn thêm payload
    protected abstract void addIdClaims(JWTClaimsSet.Builder builder, T user);

    public AccessTokenResponse generateNewAccessToken(RefreshTokenRequest refreshToken)
            throws ParseException, JOSEException {

        SignedJWT signedJWT = SignedJWT.parse(refreshToken.getRefreshToken());
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        Date expiryDate = claims.getExpirationTime();

        if (expiryDate.before(new Date())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String type = claims.getStringClaim("type");
        if (!TokenType.REFRESHTYPE.equals(type)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String username = claims.getSubject();
        T user = findUserByUsername(username);

        boolean valid = refreshTokenService.validateRefreshToken(username, refreshToken.getRefreshToken());
        if (!valid) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = generateToken(user, 15, TokenType.ACCESSTYPE);

        return AccessTokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Override
    public AccessTokenResponse refreshAccessToken(RefreshTokenRequest refreshToken) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(refreshToken.getRefreshToken());
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        if (claims.getExpirationTime().before(new Date())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (!claims.getStringClaim("type").equals(TokenType.REFRESHTYPE)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String username = claims.getSubject();
        boolean valid = refreshTokenService.validateRefreshToken(username, refreshToken.getRefreshToken());
        if (!valid) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        T user = findUserByUsername(username);
        String newAccessToken = generateToken(user, TIME_ACCESS, TokenType.ACCESSTYPE);

        return AccessTokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Override
    public void logout() throws ParseException, JOSEException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        refreshTokenService.deleteRefreshToken(authentication.getName());
        SecurityContextHolder.clearContext();
    }

    @Override
    public void logout(String username) throws ParseException, JOSEException {
        refreshTokenService.deleteRefreshToken(username);
        SecurityContextHolder.clearContext();
    }

}

