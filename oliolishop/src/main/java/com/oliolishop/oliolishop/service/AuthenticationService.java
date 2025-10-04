package com.oliolishop.oliolishop.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.oliolishop.oliolishop.constant.RedisKey;
import com.oliolishop.oliolishop.constant.TokenType;
import com.oliolishop.oliolishop.dto.Token.AccessTokenResponse;
import com.oliolishop.oliolishop.dto.Token.RefreshTokenRequest;
import com.oliolishop.oliolishop.dto.account.AccountRequest;
import com.oliolishop.oliolishop.dto.account.AccountResponse;
import com.oliolishop.oliolishop.dto.account.AccountUpdateRequest;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateRequest;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateResponse;
import com.oliolishop.oliolishop.dto.authenticate.ChangePasswordRequest;
import com.oliolishop.oliolishop.dto.authenticate.ChangePasswordResponse;
import com.oliolishop.oliolishop.dto.customer.CustomerRequest;
import com.oliolishop.oliolishop.dto.customer.CustomerResponse;
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.entity.Customer;
import com.oliolishop.oliolishop.entity.Permission;
import com.oliolishop.oliolishop.entity.Role;
import com.oliolishop.oliolishop.enums.OtpType;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.AccountMapper;
import com.oliolishop.oliolishop.mapper.CustomerMapper;
import com.oliolishop.oliolishop.repository.AccountRepository;
import com.oliolishop.oliolishop.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationService {
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;
    AccountRepository accountRepository;
    CustomerMapper customerMapper;
    RefreshTokenService refreshTokenService;
    AccountMapper accountMapper;
    PasswordEncoder passwordEncoder;
    CustomerService customerService;
    EmailService emailService;
    RedisService redisService;
    static final int TIME_EXPIRE_OTP = 3;
    static final int  TIME_ACCESS = 15; //15p
    static final int TIME_REFRESH = 24; //24h
    private final CustomerRepository customerRepository;

    public AuthenticateResponse authenticate(AuthenticateRequest request){

        var account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean result = passwordEncoder.matches(request.getPassword(),account.getPassword());
        if(!result){
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }



        String accessToken = generateToken(account,TIME_ACCESS,TokenType.ACCESSTYPE); // 15 phút
        String refreshToken = generateToken(account,TIME_REFRESH,TokenType.REFRESHTYPE);// hoặc JWT riêng

        refreshTokenService.storeRefreshToken(account.getUsername(),refreshToken,TIME_REFRESH);


        return  AuthenticateResponse.builder()
                .authenticated(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateToken(Account account, long expiryMinutes, String tokenType){
        JWSHeader header=new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet=new JWTClaimsSet.Builder()
                .subject(account.getUsername())
                .issuer("oliolishop")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES)))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope",buildScope(account))
                .claim("type",tokenType)
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }


    private String buildScope(Account account) {
        // Nếu account có customer → đây là khách hàng
        if (account.getCustomer() != null) {
            return "ROLE_CUSTOMER";
        }
        // Nếu không có customer và không có employee? (trường hợp đặc biệt)
        else {
            return "ROLE_GUEST";
        }
    }


    public AccessTokenResponse generateNewAccessToken(RefreshTokenRequest refreshToken) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(refreshToken.getRefreshToken());

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        if(!signedJWT.verify(verifier)){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        Date expiryDate =claims.getExpirationTime();
        if(expiryDate.before(new Date())){
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String type = claims.getStringClaim("type");
        if(!type.equals(TokenType.REFRESHTYPE)){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        String username = claims.getSubject();
        Account account = accountRepository.findByUsername(username).orElseThrow(()->new AppException(ErrorCode.BRAND_NOT_EXISTED));

        boolean validate = refreshTokenService.validateRefreshToken(username,refreshToken.getRefreshToken());
        if(!validate){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        String newAccessToken = generateToken(account,TIME_ACCESS,TokenType.ACCESSTYPE);
        return AccessTokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    public void
    logout() throws ParseException, JOSEException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userName = authentication.getName();
        // Xóa refresh token trong Redis theo key bạn đang dùng
        boolean check = refreshTokenService.deleteRefreshToken(userName);
        // Clear security context
        SecurityContextHolder.clearContext();
    }

    // (Tùy chọn) Logout theo username nếu bạn gọi từ nơi khác
    public void logout(String userName) throws ParseException, JOSEException {
        refreshTokenService.deleteRefreshToken(userName);
        SecurityContextHolder.clearContext();
    }

    public CustomerResponse getInfor(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new AppException(ErrorCode.UNAUTHENTICATED);
//        }
//
//        if(authentication.getPrincipal() instanceof String principalStr){
//            if("anonymousUser".equals(principalStr)) throw new AppException(ErrorCode.INVALID_TOKEN);
//        }
        Authentication authentication=getAuthentication();

        String username = authentication.getName();

        Account a = accountRepository.findByUsername(username).orElseThrow(()->new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));
        Customer c = a.getCustomer();
        return customerMapper.toResponse(c);
    }

    public void sendOtp(String email, OtpType type) {
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        String redisKey = RedisKey.OTP_PREFIX + type.name() + ":" + email;// random 6 số
        switch (type) {
            case REGISTER -> {
                redisService.set(redisKey, otp, TIME_EXPIRE_OTP * 60);
                emailService.sendOtp(email, otp);
            }
            case RESET_PASSWORD -> {
                boolean check = accountRepository.existsByEmail(email);
                if (!check) {
                    throw new AppException(ErrorCode.ACCOUNT_NOT_EXISTED);
                }
                redisService.set(redisKey, otp, TIME_EXPIRE_OTP * 60);
                emailService.sendOtp(email, otp);
            }
            default -> throw new AppException(ErrorCode.INVALID_OTP);
        }
    }

    public boolean verifyOtp(String otp, String email, OtpType type) {
        String key = RedisKey.OTP_PREFIX +type+":"+ email;
        String storedOtp = redisService.get(key, String.class);

        if (storedOtp.isEmpty() || storedOtp == null)
            throw new AppException(ErrorCode.INVALID_OTP);
        boolean match = storedOtp.equals(otp);
        if (match) redisService.delete(key);
        return match;
    }
    private Authentication getAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (authentication.getPrincipal() instanceof String principalStr) {
            if ("anonymousUser".equals(principalStr)) throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        return authentication;
    }

    public AccountResponse createAccount(AccountRequest request){
        boolean existedUsername = accountRepository.existsByUsername(request.getUsername());
        if(existedUsername){
            throw new AppException(ErrorCode.ACCOUNT_EXISTED);
        }
        boolean existedEmail = accountRepository.existsByEmail(request.getEmail());
        if(existedEmail){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }


        Account account = accountMapper.toAccount(request);
        String id = UUID.randomUUID().toString();
        String password = passwordEncoder.encode(request.getPassword());
        account.setId(id);
        account.setPassword(password);
        CustomerRequest customer = request.getCustomerRequest();

        AccountResponse result = accountMapper.toAccountResponse(accountRepository.save(account));

        result.setCustomerResponse(customerService.createCustomer(customer,id));

        return result;
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request) throws ParseException, JOSEException {
        Authentication authentication = getAuthentication();
        String username = authentication.getName();
        Account existAccount = accountRepository.findByUsername(username).orElseThrow(()->new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        boolean check = passwordEncoder.matches(request.getOldPassword(),existAccount.getPassword());

        if(!check){
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        boolean match = request.getNewPassword().equals(request.getReNewPassword());
        if(!match){
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        String hashPassword = passwordEncoder.encode(request.getNewPassword());
        existAccount.setPassword(hashPassword);
        accountRepository.save(existAccount);
        refreshTokenService.deleteRefreshToken(username);

        String newRefreshToken = generateToken(existAccount,TIME_REFRESH,TokenType.REFRESHTYPE);
        String newAccessToken = generateToken(existAccount,TIME_ACCESS,TokenType.ACCESSTYPE);

        refreshTokenService.storeRefreshToken(username,newRefreshToken,TIME_REFRESH);

        return ChangePasswordResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void resetPassword(String newPassword,String email){
        Account existAccount = accountRepository.findByEmail(email).orElseThrow(()->new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));
        String encodePassword = passwordEncoder.encode(newPassword);

        existAccount.setPassword(encodePassword);
        accountRepository.save(existAccount);
    }

    public AccountResponse updateAccount(AccountUpdateRequest request){
        Authentication authentication = getAuthentication();
        String username = authentication.getName();

        Account existAccount = accountRepository.findByUsername(username).orElseThrow(()-> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        Customer existCustomer = customerRepository.findByAccountId(existAccount.getId()).orElseThrow(()-> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        existAccount.setPhoneNumber(request.getPhoneNumber());

        existCustomer.setName(request.getName());

        existCustomer.setDob(request.getDob());

        existCustomer.setGender(request.getGender());

        AccountResponse response = accountMapper.toAccountResponse(accountRepository.save(existAccount));

        response.setCustomerResponse(customerMapper.toResponse(customerRepository.save(existCustomer)));

        return response;
    }
}
