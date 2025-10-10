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
    PASSWORD_INVALID(1005, "Password must be at least 8 characters and contain at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character", HttpStatus.BAD_REQUEST),
    VALUE_REQUIRED(1006, "Value is required", HttpStatus.BAD_REQUEST),
    NAME_REQUIRED(1007,"Name is required",HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1008, "Password and re-entered password do not match", HttpStatus.BAD_REQUEST),
    QUANTITY_POSITIVE(1009,"Quantity must be greater than 0",HttpStatus.BAD_REQUEST),
    PRICE_POSITIVE(1010,"Price must be greater than 0",HttpStatus.BAD_REQUEST),
    // 2xxx: auth
    UNAUTHENTICATED(2001,"Unauthenticated",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002,"You do not have permission",HttpStatus.FORBIDDEN),
    REFRESH_TOKEN_EXPIRED(2003,"Refresh token has expired",HttpStatus.FORBIDDEN),
    INVALID_TOKEN(2004,"Token is invalid", HttpStatus.UNAUTHORIZED),
    INVALID_OTP(2005, "Mã OTP không hợp lệ",HttpStatus.FORBIDDEN),
    OTP_EXPIRED(2006,"Mã Otp đã hết hạn",HttpStatus.FORBIDDEN),
    // 3xxx: business logic
    ACCOUNT_EXISTED(3001,"Account existed",HttpStatus.CONFLICT),
    ACCOUNT_NOT_EXISTED(3002, "Account does not exist",HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(3003, "Password is incorrect",HttpStatus.UNAUTHORIZED),
    BRAND_NOT_EXISTED(3004, "Brand does not exist",HttpStatus.NOT_FOUND),
    BRAND_EXISTED(3005,"Brand existed",HttpStatus.CONFLICT),
    CATEGORY_NOT_EXIST(3006,"Category does not exist",HttpStatus.NOT_FOUND),
    PRODUCT_NOT_EXIST(3007,"product does not exist", HttpStatus.NOT_FOUND),
    ATTRIBUTE_EXISTED(3008, "Description Attribute existed", HttpStatus.NOT_FOUND),
    ATTRIBUTE_NOT_EXIST(3009, "Description Attribute does not exist",HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(3010,"Email existed",HttpStatus.CONFLICT),
    EMPTY_PRODUCT_SKU(3012,"List Product Sku is empty",HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST(3011,"Role does not exist",HttpStatus.NOT_FOUND),
    CUSTOMER_NOT_EXISTED(3012,"Customer does not exist",HttpStatus.NOT_FOUND),
    DISCOUNT_RULE_NOT_EXISTED(3013,"Discount rule does not exist",HttpStatus.NOT_FOUND),
    CART_EXISTED(3014,"Cart existed",HttpStatus.CONFLICT),
    CART_NOT_EXISTED(3015,"Cart does not exist",HttpStatus.NOT_FOUND),
    INVALID_REQUEST(3016, "Request không hợp lệ",HttpStatus.BAD_REQUEST),
    PHONE_EXISTED(3017,"Phone has existed",HttpStatus.CONFLICT),
    // 9xxx: system
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    ;
    private int Code;
    private String message;
    private HttpStatusCode statusCode;



}
