package com.example.demo.config;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtProvider {

    private final SecretKey key =
            Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    // ✅ CREATE JWT
    public String generateToken(Authentication authentication) {

        String authority = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER");

        // 🔴 REMOVE "ROLE_" IF PRESENT
        String role = authority.startsWith("ROLE_")
                ? authority.substring(5)
                : authority;

        return Jwts.builder()
                .setSubject(authentication.getName())   // email
                .claim("role", role)                    // ADMIN / USER
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(key)
                .compact();
    }


    // ✅ EXTRACT EMAIL SAFELY
    public String getEmailFromToken(String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        token = token.substring(7);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)   // ✅ CORRECT
                .getBody();

        return claims.getSubject(); // email
    }

    // ✅ OPTIONAL: ROLE EXTRACTION (USEFUL LATER)
    public String getRoleFromToken(String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Invalid JWT token");
        }
        // Accept either raw JWT or "Bearer <jwt>"
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("role", String.class);
    }
}
