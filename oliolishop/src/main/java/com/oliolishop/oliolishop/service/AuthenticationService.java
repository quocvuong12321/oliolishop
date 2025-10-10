package com.oliolishop.oliolishop.service;

import com.nimbusds.jose.JOSEException;
import com.oliolishop.oliolishop.dto.Token.AccessTokenResponse;
import com.oliolishop.oliolishop.dto.Token.RefreshTokenRequest;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateRequest;
import com.oliolishop.oliolishop.dto.authenticate.AuthenticateResponse;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticateResponse authenticate(AuthenticateRequest request);
    AccessTokenResponse refreshAccessToken(RefreshTokenRequest request) throws ParseException, JOSEException;
    void logout() throws ParseException, JOSEException;
    void logout(String username) throws ParseException, JOSEException;
}