package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtConstant {

    public static String SECRET_KEY;

    public static final String JWT_HEADER = "Authorization";

    @Value("${jwt.secret}")
    public void setSecretKey(String secretKey) {
        JwtConstant.SECRET_KEY = secretKey;
    }
}
