package com.oliolishop.oliolishop.controller;


import com.nimbusds.jose.JOSEException;
import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.Token.AccessTokenResponse;
import com.oliolishop.oliolishop.dto.Token.RefreshTokenRequest;
import com.oliolishop.oliolishop.dto.account.AccountRequest;
import com.oliolishop.oliolishop.dto.account.AccountResponse;
import com.oliolishop.oliolishop.dto.account.AccountUpdateRequest;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.authenticate.*;
import com.oliolishop.oliolishop.dto.customer.CustomerResponse;
import com.oliolishop.oliolishop.enums.OtpType;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.service.CustomerAuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping(ApiPath.BASE+ApiPath.AUTHENTICATION)
public class AuthenticationController {
    @Autowired
    private CustomerAuthenticationService authenticationService;


    @PostMapping
    public ApiResponse<AuthenticateResponse> authenticate(@RequestBody @Valid AuthenticateRequest request){
        return ApiResponse.<AuthenticateResponse>builder()
                .result(authenticationService.authenticate(request))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AccessTokenResponse> refresh(@RequestBody RefreshTokenRequest request) throws ParseException, JOSEException {
        return ApiResponse.<AccessTokenResponse>builder()
                .result(authenticationService.generateNewAccessToken(request))
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() throws ParseException, JOSEException {
        authenticationService.logout();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ApiResponse<AccountResponse> getMyInfor(){
        return ApiResponse.<AccountResponse>builder()
                .result(authenticationService.getInfor())
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<AccountResponse> verifyCreateAccount(@Valid @RequestBody VerifyOtpRequest request){

        if(!request.getType().equals(OtpType.REGISTER))
            throw new AppException(ErrorCode.INVALID_OTP);
        AccountRequest account = authenticationService.verifyRegisterOtp(request.getOtp(),request.getEmail());
        if(account == null)
            throw new AppException(ErrorCode.INVALID_OTP);
        return  ApiResponse.<AccountResponse>builder()
                .result(authenticationService.createAccount(account))
                .build();
    }
    @PostMapping("/register/send-otp")
    public ApiResponse<String> createAccount(@Valid @RequestBody AccountRequest request){
        authenticationService.handleRegisterOtp(request);

        return ApiResponse.<String>builder()
                .result(String.format(MessageConstants.OTP_SENT,request.getEmail()))
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request) throws ParseException, JOSEException {
        return ApiResponse.<ChangePasswordResponse>builder()
                .result(authenticationService.changePassword(request))
                .build();
    }



    @PostMapping("/send-otp")
    public ApiResponse<String> sendOtp(@RequestBody SendOtpRequest request){

        authenticationService.handleOtp(request);

        return ApiResponse.<String>builder()
                .result(String.format(MessageConstants.OTP_SENT,request.getEmail()))
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<Boolean> verifyOtp(@RequestBody VerifyOtpRequest request){
        Boolean verify = authenticationService.verifyOtp(request);
        if(!verify)
            throw new AppException(ErrorCode.INVALID_OTP);
        return ApiResponse.<Boolean>builder()
                .result(verify)
                .build();
    }


    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@Valid @RequestBody ForgetPasswordRequest request){
        authenticationService.resetPassword(request.getNewPassword(), request.getEmail());
        return  ApiResponse.<String>builder()
                .result(String.format(MessageConstants.PASSWORD_RESET_SUCCESS))
                .build();
    }

    @PostMapping("/update-account")
    public ApiResponse<AccountResponse> updateAccount(@RequestBody AccountUpdateRequest request){

        return ApiResponse.<AccountResponse>builder()
                .result(authenticationService.updateAccount(request))
                .build();
    }
}
