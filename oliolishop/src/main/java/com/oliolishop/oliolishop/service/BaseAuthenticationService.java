package com.oliolishop.oliolishop.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.oliolishop.oliolishop.entity.Employee;
import com.oliolishop.oliolishop.entity.Permission;
import com.oliolishop.oliolishop.entity.Role;
import com.oliolishop.oliolishop.exception.AuthCookieExpiredException;
import com.oliolishop.oliolishop.util.ClearCookies;
import jakarta.servlet.http.Cookie;
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
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseAuthenticationService<T> implements AuthenticationService {
    protected final RefreshTokenService refreshTokenService;
    protected final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    @NonFinal
    @Value("${jwt.signerKey}")
    protected  String SIGNER_KEY;
    public static final long TIME_ACCESS = 20;       // 20 phút
    public static final long TIME_REFRESH = 20 * 24 * 60; // 20 ngày


    protected abstract T findUserByUsername(String username);
    protected abstract String getUsername(T user);
    protected abstract String getPassword(T user);
    protected abstract String buildScope(T user);
    protected abstract Account.AccountStatus getStatus(T user);
    protected abstract Set<String> getPermission(T user);
    protected abstract String getCookieTokenType();


    @Override
    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        T user = findUserByUsername(request.getUsername());
        if (user == null) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_EXISTED);
        }

        if (!passwordEncoder.matches(request.getPassword(), getPassword(user))) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        if(!Account.AccountStatus.Active.equals(getStatus(user)))
            throw new AppException(ErrorCode.ACCOUNT_HAS_BLOCKED);


        String accessToken = generateToken(user, TIME_ACCESS, TokenType.ACCESSTYPE);
        String refreshToken = generateToken(user, TIME_REFRESH , TokenType.REFRESHTYPE);

        refreshTokenService.storeRefreshToken(getUsername(user), refreshToken, (int)TIME_REFRESH);

        String role = buildScope(user);

        Set<String> permissions=getPermission(user);

        return AuthenticateResponse.builder()
                .authenticated(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(role)
                .permissions(permissions)
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


    private JWTClaimsSet validateAndExtractClaims(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        // 1. Kiểm tra chữ ký
        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        // 2. Kiểm tra loại token (phải là Refresh Token)
        String type = claims.getStringClaim("type");
        if (!TokenType.REFRESHTYPE.equals(type)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        // 3. Kiểm tra tính hợp lệ trong hệ thống (DB/Cache)
        String username = claims.getSubject();
        boolean valid = refreshTokenService.validateRefreshToken(username, token);
        if (!valid) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        return claims;
    }

    public AccessTokenResponse generateNewAccessToken(String refreshToken)
            throws ParseException, JOSEException {



        JWTClaimsSet claims = validateAndExtractClaims(refreshToken);
        Date expiryDate = claims.getExpirationTime();

        if (expiryDate.before(new Date())) {
            throw new AuthCookieExpiredException(ErrorCode.REFRESH_TOKEN_EXPIRED, getCookieTokenType());
        }


        String username = claims.getSubject();
        T user = findUserByUsername(username);


        String newAccessToken = generateToken(user, TIME_ACCESS, TokenType.ACCESSTYPE);

        return AccessTokenResponse.builder()
                .accessToken(newAccessToken)
                .role(buildScope(user))
                .permissions(getPermission(user))
                .build();
    }

    @Override
    public AccessTokenResponse refreshAccessToken(RefreshTokenRequest refreshToken) throws ParseException, JOSEException {
        return generateNewAccessToken(refreshToken.getRefreshToken());
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

