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
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.enums.OtpType;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.service.AuthenticationService;
import com.oliolishop.oliolishop.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping(ApiPath.BASE+ApiPath.AUTHENTICATION)
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;


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
    public ApiResponse<CustomerResponse> getMyInfor(){
        return ApiResponse.<CustomerResponse>builder()
                .result(authenticationService.getInfor())
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<AccountResponse> createAccount(@Valid @RequestBody RegisterRequest request){

        boolean valid = authenticationService.verifyOtp(request.getOtp(),request.getAccountRequest().getEmail(),OtpType.REGISTER);
        if(!valid)
            throw new AppException(ErrorCode.INVALID_OTP);
        AccountResponse response = authenticationService.createAccount(request.getAccountRequest());
        return  ApiResponse.<AccountResponse>builder()
                .result(response)
                .build();
    }
    @PostMapping("/change-password")
    public ApiResponse<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request) throws ParseException, JOSEException {
        return ApiResponse.<ChangePasswordResponse>builder()
                .result(authenticationService.changePassword(request))
                .build();
    }

    @PostMapping("/send-otp")
    public ApiResponse<String> sendOtp(@RequestParam String email, @RequestParam OtpType type) {
        authenticationService.sendOtp(email, type);
        return ApiResponse.<String>builder()
                .result(String.format(MessageConstants.OTP_SENT,email))
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody ForgetPasswordRequest request){
        boolean check = request.getNewPassword().equals(request.getReNewPassword());
        if(!check){
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        boolean verify = authenticationService.verifyOtp(request.getOtp(),request.getEmail(),OtpType.RESET_PASSWORD);
        if(!verify)
        {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        authenticationService.resetPassword(request.getNewPassword(), request.getEmail());
        return ApiResponse.<String>builder()
                .result(MessageConstants.PASSWORD_RESET_SUCCESS)
                .build();
    }

    @PostMapping("/update-account")
    public ApiResponse<AccountResponse> updateAccount(@RequestBody AccountUpdateRequest request){

        return ApiResponse.<AccountResponse>builder()
                .result(authenticationService.updateAccount(request))
                .build();
    }
}
