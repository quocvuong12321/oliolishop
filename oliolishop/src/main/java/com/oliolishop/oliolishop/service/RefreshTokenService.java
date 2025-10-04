package com.oliolishop.oliolishop.service;

import com.nimbusds.jose.JOSEException;
import com.oliolishop.oliolishop.constant.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private static final String KEY= TokenType.REFRESHTYPE+":";
    private static final String BLACKLIST_REFRESH_TOKEN_KEY = "blacklist_refresh:";
    public String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeRefreshToken(String userName, String refreshToken, int durationHours) {
        String hashedToken = sha256(refreshToken);
        long timeExpire = (long)durationHours*60*60;
//        redisTemplate.opsForValue().set("refresh:" + userName, hashedToken, durationHours, TimeUnit.HOURS);
        redisService.set(KEY+userName,hashedToken,timeExpire);
    }

    public boolean validateRefreshToken(String userName, String refreshToken) {
        String hashedToken =  redisService.get(KEY+userName, String.class);
        return hashedToken != null && sha256(refreshToken).equals(hashedToken);
    }

    public boolean deleteRefreshToken(String userName) throws ParseException, JOSEException {
        return redisService.delete(KEY+userName);
    }

    public void blacklistRefreshToken(String refreshToken, String username){
        String hashedToken = sha256(refreshToken);
        long remainingTTL  = getRefreshTokenTTL(username);

        if (remainingTTL <= 0) {
            remainingTTL = AuthenticationService.TIME_REFRESH * 60 * 60; // 24 giờ
        }

        redisService.set(BLACKLIST_REFRESH_TOKEN_KEY + hashedToken, "blacklisted", remainingTTL);
    }

    public long getRefreshTokenTTL(String username){
        return redisService.getTTL(KEY+username);
    }

    public  boolean isRefreshTokenBlacklisted(String refreshToken){
        String hashedToken = sha256(refreshToken);
        String value =redisService.get(BLACKLIST_REFRESH_TOKEN_KEY+hashedToken,String.class);
        return !value.isEmpty()&&"blacklisted".equals(value);
    }

    public void logout(String username,String refreshToken) throws ParseException, JOSEException {
        if(!refreshToken.isEmpty()){
            blacklistRefreshToken(refreshToken,username);
        }
        deleteRefreshToken(refreshToken);
    }

}
