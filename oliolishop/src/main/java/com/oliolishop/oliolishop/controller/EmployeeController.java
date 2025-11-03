package com.oliolishop.oliolishop.controller;


import com.nimbusds.jose.JOSEException;
import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.Token.AccessTokenResponse;
import com.oliolishop.oliolishop.dto.Token.RefreshTokenRequest;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateRequest;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateResponse;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.service.BaseAuthenticationService;
import com.oliolishop.oliolishop.service.EmployeeAuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping(ApiPath.Employee.ROOT)
public class EmployeeController {
    @Autowired
    private EmployeeAuthenticationService authenticationService;


    @PostMapping
    public ApiResponse<AuthenticateResponse> authenticate(@RequestBody AuthenticateRequest request,
                                                          HttpServletResponse response){

        AuthenticateResponse responseAuth = authenticationService.authenticate(request);
        Cookie cookie = new Cookie("refreshToken-employee",responseAuth.getRefreshToken());
        cookie.setMaxAge((int)(BaseAuthenticationService.TIME_REFRESH*60));
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ApiResponse.<AuthenticateResponse>builder()
                .result(
                        AuthenticateResponse.builder()
                                .authenticated(true)
                                .accessToken(responseAuth.getAccessToken())
                                .refreshToken(MessageConstants.REFRESH_TOKE_SAVED)
                                .build()
                )
                .build();
    }
    @PostMapping(ApiPath.Employee.LOGOUT)
    public ResponseEntity<Void> logout() throws ParseException, JOSEException {
        authenticationService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiPath.Employee.REFRESH)
    public ApiResponse<AccessTokenResponse> refreshToken(HttpServletRequest request) throws ParseException, JOSEException {

        Cookie[] cookies = request.getCookies();

        if(cookies == null)
            throw new AppException(ErrorCode.INVALID_TOKEN);

        String refreshToken = null;
        for(var cookie: cookies){
            if("refreshToken-employee".equals(cookie.getName())){
                refreshToken = cookie.getValue();
                break;
            }
        }
        if(refreshToken == null) throw new AppException(ErrorCode.INVALID_TOKEN);

        AccessTokenResponse response = authenticationService.refreshAccessToken(RefreshTokenRequest.builder()
                        .refreshToken(refreshToken)
                .build());

        return ApiResponse.<AccessTokenResponse>builder()
                .result(response)
                .build();
    }


}
