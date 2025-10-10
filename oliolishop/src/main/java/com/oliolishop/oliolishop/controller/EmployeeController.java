package com.oliolishop.oliolishop.controller;


import com.nimbusds.jose.JOSEException;
import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateRequest;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateResponse;
import com.oliolishop.oliolishop.service.EmployeeAuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping(ApiPath.Employee.ROOT)
public class EmployeeController {
    @Autowired
    private EmployeeAuthenticationService authenticationService;


    @PostMapping
    public ApiResponse<AuthenticateResponse> authenticate(@RequestBody AuthenticateRequest request){
        return ApiResponse.<AuthenticateResponse>builder()
                .result(authenticationService.authenticate(request))
                .build();
    }
    @PostMapping(ApiPath.Employee.LOGOUT)
    public ResponseEntity<Void> logout() throws ParseException, JOSEException {
        authenticationService.logout();
        return ResponseEntity.ok().build();
    }

}
