package com.oliolishop.oliolishop.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class ClearCookies {


    public static void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null); // Giá trị null
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);       // Rất quan trọng: MaxAge = 0 để trình duyệt xóa
        response.addCookie(cookie);
    }
}
