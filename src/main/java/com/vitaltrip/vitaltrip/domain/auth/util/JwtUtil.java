package com.vitaltrip.vitaltrip.domain.auth.util;

import com.vitaltrip.vitaltrip.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtUtil(
        @Value("${jwt.secret:mySecretKeyForJWTTokenGeneration123456789}") String secret,
        @Value("${jwt.access-token-expiration:3600000}") long accessTokenExpiration, // 1시간
        @Value("${jwt.refresh-token-expiration:604800000}") long refreshTokenExpiration // 7일
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpiration);
    }

    public String generateTempToken(User user) {
        long tempTokenExpiration = 1800000;

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tempTokenExpiration);

        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("name", user.getName())
            .claim("temp", true)
            .claim("role", "TEMP_USER")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }

    private String generateToken(User user, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("name", user.getName())
            .claim("countryCode", user.getCountryCode())
            .claim("role", user.getRole().name())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (JwtException e) {
            log.error("JWT parsing error: {}", e.getMessage());
            throw e;
        }
    }

    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    public String getCountryCode(String token) {
        return getClaims(token).get("countryCode", String.class);
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTempToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.get("temp", Boolean.class) != null &&
                claims.get("temp", Boolean.class);
        } catch (JwtException e) {
            return false;
        }
    }

}
