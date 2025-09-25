package com.oliolishop.oliolishop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    // 1xxx: validation
    INVALID_KEY(1001,"Invalid message key",HttpStatus.BAD_REQUEST),
    EMPTY_USERNAME(1002,"Username is empty",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"Username at least {min} characters",HttpStatus.BAD_REQUEST),
    DOB_INVALID(1004,"Your age must be at least {min}",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1005, "Password must at least {min} characters", HttpStatus.BAD_REQUEST),
    VALUE_REQUIRED(1006, "Value is required", HttpStatus.BAD_REQUEST),
    NAME_REQUIRED(1007,"Name is required",HttpStatus.BAD_REQUEST),

    // 2xxx: auth
    UNAUTHENTICATED(2001,"Unauthenticated",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002,"You do not have permission",HttpStatus.FORBIDDEN),
    REFRESH_TOKEN_EXPIRED(2003,"Refresh token has expired",HttpStatus.FORBIDDEN),
    INVALID_REFRESH_TOKEN(2004,"Refresh token is invalid", HttpStatus.UNAUTHORIZED),
    EMPTY_PRODUCT_SKU(3010,"List Product Sku is empty",HttpStatus.BAD_REQUEST),
    // 3xxx: business logic
    USER_EXISTED(3001,"User existed",HttpStatus.CONFLICT),
    USER_NOT_EXISTED(3002, "User does not exist",HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(3003, "Password is incorrect",HttpStatus.UNAUTHORIZED),
    BRAND_NOT_EXISTED(3004, "Brand does not exist",HttpStatus.NOT_FOUND),
    BRAND_EXISTED(3005,"Brand existed",HttpStatus.CONFLICT),
    CATEGORY_NOT_EXIST(3006,"Category does not exist",HttpStatus.NOT_FOUND),
    PRODUCT_NOT_EXIST(3007,"product does not exist", HttpStatus.NOT_FOUND),
    ATTRIBUTE_EXISTED(3008, "Description Attribute existed", HttpStatus.NOT_FOUND),
    ATTRIBUTE_NOT_EXIST(3009, "Description Attribute does not exist",HttpStatus.NOT_FOUND),
    // 9xxx: system
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    ;
    private int Code;
    private String message;
    private HttpStatusCode statusCode;



}
