package com.oliolishop.oliolishop.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthCookieExpiredException extends AppException {
    private final String cookieName;

    public AuthCookieExpiredException(ErrorCode errorCode, String cookieName) {
        super(errorCode);
        this.cookieName = cookieName;
    }

    public String getCookieName() {
        return cookieName;
    }
}