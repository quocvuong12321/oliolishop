package com.oliolishop.oliolishop.controller;


import com.nimbusds.jose.JOSEException;
import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.constant.TokenType;
import com.oliolishop.oliolishop.dto.Token.AccessTokenResponse;
import com.oliolishop.oliolishop.dto.Token.RefreshTokenRequest;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateRequest;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateResponse;
import com.oliolishop.oliolishop.dto.employee.ChangePasswordRequest;
import com.oliolishop.oliolishop.dto.employee.EmployeeResponse;
import com.oliolishop.oliolishop.dto.employee.EmployeeUpdateRequest;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.service.BaseAuthenticationService;
import com.oliolishop.oliolishop.service.EmployeeAuthenticationService;
import com.oliolishop.oliolishop.service.EmployeeService;
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
    @Autowired
    private EmployeeService employeeService;

    @PostMapping(ApiPath.Employee.LOGIN)
    public ApiResponse<AuthenticateResponse> authenticate(@RequestBody AuthenticateRequest request,
                                                          HttpServletResponse response){

        AuthenticateResponse responseAuth = authenticationService.authenticate(request);
        Cookie cookie = new Cookie("refreshToken-employee",responseAuth.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)(BaseAuthenticationService.TIME_REFRESH*60));
        response.addCookie(cookie);

        return ApiResponse.<AuthenticateResponse>builder()
                .result(
                        AuthenticateResponse.builder()
                                .authenticated(true)
                                .accessToken(responseAuth.getAccessToken())
                                .refreshToken(MessageConstants.REFRESH_TOKE_SAVED)
                                .permissions(responseAuth.getPermissions())
                                .role(responseAuth.getRole())
                                .mustChangePassword(responseAuth.isMustChangePassword())
                                .build()
                )
                .build();
    }
    @PostMapping(ApiPath.Employee.LOGOUT)
    public ResponseEntity<Void> logout() throws ParseException, JOSEException {
        authenticationService.logout();

        Cookie cookie = new Cookie(TokenType.COOKIE_REFRESH_EMPLOYEE, null); // Đặt giá trị là null
        cookie.setHttpOnly(true);
        cookie.setSecure(true);         // Phải giống khi tạo
        cookie.setPath("/");            // Phải giống khi tạo
        cookie.setMaxAge(0);

        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiPath.Employee.REFRESH)
    public ApiResponse<AccessTokenResponse> refreshToken(HttpServletRequest request) throws ParseException, JOSEException {

        Cookie[] cookies = request.getCookies();

        if(cookies == null)
            throw new AppException(ErrorCode.INVALID_TOKEN);

        String refreshToken = null;
        for(var cookie: cookies){
            if(TokenType.COOKIE_REFRESH_EMPLOYEE.equals(cookie.getName())){
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

    @PutMapping
    public ApiResponse<EmployeeResponse> updateEmployee(@Valid @RequestBody EmployeeUpdateRequest request){

        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.updateEmployee(request))
                .build();
    }

    @PutMapping (ApiPath.Employee.PASSWORD)
    ApiResponse<String> updatePassword(@Valid @RequestBody ChangePasswordRequest request){
        employeeService.updatePassword(request);
        return ApiResponse.<String>builder()
                .result(String.format(MessageConstants.SUCCESS,"Đổi mật khẩu"))
                .build();
    }

    @GetMapping(ApiPath.Employee.PROFILE)
    ApiResponse<EmployeeResponse> getProfile(){
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.getProfile())
                .build();
    }



}
