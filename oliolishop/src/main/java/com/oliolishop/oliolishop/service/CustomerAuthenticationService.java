package com.oliolishop.oliolishop.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.oliolishop.oliolishop.constant.TokenType;
import com.oliolishop.oliolishop.dto.account.AccountRequest;
import com.oliolishop.oliolishop.dto.account.AccountResponse;
import com.oliolishop.oliolishop.dto.account.AccountUpdateRequest;
import com.oliolishop.oliolishop.dto.authenticate.*;
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.entity.Customer;
import com.oliolishop.oliolishop.enums.OtpType;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.AccountMapper;
import com.oliolishop.oliolishop.mapper.CustomerMapper;
import com.oliolishop.oliolishop.repository.AccountRepository;
import com.oliolishop.oliolishop.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerAuthenticationService extends BaseAuthenticationService<Account> {
    //    @NonFinal
//    @Value("${jwt.signerKey}")
//    protected String SIGNER_KEY;
    static final int TIME_EXPIRE_OTP = 5;
//    static final int TIME_ACCESS = 15; //15p
//    static final int TIME_REFRESH = 24; //24h
    private final AccountRepository accountRepository;
    private final CustomerMapper customerMapper;
    private final AccountMapper accountMapper;
    private final EmailService emailService;
    private final RedisService redisService;
    private final CustomerRepository customerRepository;

    public CustomerAuthenticationService(
            RefreshTokenService refreshTokenService,
            AccountRepository accountRepository,
            CustomerMapper customerMapper,
            AccountMapper accountMapper,
            EmailService emailService,
            RedisService redisService,
            CustomerRepository customerRepository
    ) {
        super(refreshTokenService);
        this.accountRepository = accountRepository;
        this.customerMapper = customerMapper;
        this.accountMapper = accountMapper;
        this.emailService = emailService;
        this.redisService = redisService;
        this.customerRepository = customerRepository;
    }


    @Override
    protected void addCustomClaims(JWTClaimsSet.Builder builder, Account user) {

        builder.claim("customerId",user.getCustomer().getId());
    }

    @Override
    protected Account findUserByUsername(String username) {
        return accountRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));
    }

    @Override
    protected String getUsername(Account user) {
        return user.getUsername();
    }

    @Override
    protected String getPassword(Account user) {
        return user.getPassword();
    }

    @Override
    protected String buildScope(Account user) {
        return "ROLE_CUSTOMER";
    }

    public Boolean verifyOtp(VerifyOtpRequest request){
        String key = getRedisKeyOtp(request.getEmail(), request.getType());

        String storedOtp = redisService.get(key, String.class);

        if (storedOtp == null) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        if (!storedOtp.equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        String verifiedKey = getRedisKeyVerify(request.getEmail(),request.getType());
        redisService.set(verifiedKey, true, TIME_EXPIRE_OTP * 60); // 5 phút

        redisService.delete(key);
        return true;
    }

    public AccountResponse getInfor() {
        Authentication authentication = getAuthentication();
        String username = authentication.getName();

        Account a = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));
        AccountResponse response = accountMapper.toAccountResponse(a);
        Customer c = a.getCustomer();
        response.setCustomerResponse(customerMapper.toResponse(c));
        return response;
    }

    public static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if (authentication.getPrincipal() instanceof String principalStr &&
                "anonymousUser".equals(principalStr)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        return authentication;
    }

    public AccountResponse createAccount(AccountRequest request) {
        if (accountRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.ACCOUNT_EXISTED);
        if (accountRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        Account account = accountMapper.toAccount(request);
        account.setId(UUID.randomUUID().toString());

        String customerId = UUID.randomUUID().toString();
        Customer customer = Customer.builder()
                .id(customerId)
                .account(account)
                .build();


        Account response = accountRepository.save(account);
        customerRepository.save(customer);


        return accountMapper.toAccountResponse(response);
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request) throws ParseException, JOSEException {
        Authentication authentication = getAuthentication();
        String username = authentication.getName();

        Account existAccount = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), existAccount.getPassword()))
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);

        if (!request.getNewPassword().equals(request.getReNewPassword()))
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);

        existAccount.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(existAccount);
        refreshTokenService.deleteRefreshToken(username);

        String newRefreshToken = generateToken(existAccount, TIME_REFRESH, TokenType.REFRESHTYPE);
        String newAccessToken = generateToken(existAccount, TIME_ACCESS, TokenType.ACCESSTYPE);

        refreshTokenService.storeRefreshToken(username, newRefreshToken, (int)TIME_REFRESH);

        return ChangePasswordResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public AccountResponse updateAccount(AccountUpdateRequest request) {
        Authentication authentication = getAuthentication();
        String username = authentication.getName();

        Account existAccount = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        Customer existCustomer = customerRepository.findByAccountId(existAccount.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        existAccount.setPhoneNumber(request.getPhoneNumber());
        existCustomer.setName(request.getName());
        existCustomer.setDob(request.getDob());
        existCustomer.setGender(request.getGender());

        AccountResponse response = accountMapper.toAccountResponse(accountRepository.save(existAccount));
        response.setCustomerResponse(customerMapper.toResponse(customerRepository.save(existCustomer)));
        return response;
    }

    // ========================
    // OTP & EMAIL
    // ========================

    public void handleRegisterOtp(AccountRequest request) {
        String email = request.getEmail();

        if (accountRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.ACCOUNT_EXISTED);
        if (accountRepository.existsByEmail(email))
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        if (accountRepository.existsByPhoneNumber(request.getPhoneNumber()))
            throw new AppException(ErrorCode.PHONE_EXISTED);

        String otp = generateOtp();
        String redisKey = getRedisKeyOtp(email, OtpType.REGISTER);

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        RegisterRequest cache = RegisterRequest.builder()
                .otp(otp)
                .accountRequest(request)
                .build();

        redisService.set(redisKey, cache, TIME_EXPIRE_OTP * 60);
        emailService.sendOtp(email, otp);
    }

    public AccountRequest verifyRegisterOtp(String otp, String email) {
        String key = getRedisKeyOtp(email, OtpType.REGISTER);
        RegisterRequest request = redisService.get(key, RegisterRequest.class);

        if (request == null) throw new AppException(ErrorCode.OTP_EXPIRED);
        if (!otp.equals(request.getOtp())) throw new AppException(ErrorCode.INVALID_OTP);

        String verifiedKey = getRedisKeyVerify(email, OtpType.REGISTER);
        redisService.set(verifiedKey, true, TIME_EXPIRE_OTP * 60);
        redisService.delete(key);
        return request.getAccountRequest();
    }

    public void handleOtp(SendOtpRequest request) {
        if (!accountRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.ACCOUNT_NOT_EXISTED);

        String otp = generateOtp();
        String redisKey = getRedisKeyOtp(request.getEmail(), request.getType());

        redisService.set(redisKey, otp, TIME_EXPIRE_OTP * 60);
        emailService.sendOtp(request.getEmail(), otp);
    }


    public void resetPassword(String newPassword, String email) {
        String verifiedKey = getRedisKeyVerify(email, OtpType.RESET_PASSWORD);
        Boolean verified = redisService.get(verifiedKey, Boolean.class);

        if (verified == null || !verified)
            throw new AppException(ErrorCode.INVALID_OTP);

        Account existAccount = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        existAccount.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(existAccount);
        redisService.delete(verifiedKey);
    }

    // ========================
    // PRIVATE SUPPORT METHODS
    // ========================

    private String getRedisKeyOtp(String email, OtpType type) {
        return "OTP:" + type + ":" + email;
    }

    private String getRedisKeyVerify(String email, OtpType type) {
        return "OTP_VERIFIED:" + type + ":" + email;
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }


}
