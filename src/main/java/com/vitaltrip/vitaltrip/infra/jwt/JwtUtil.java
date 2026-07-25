package com.vitaltrip.vitaltrip.infra.jwt;

import com.vitaltrip.vitaltrip.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final JwtProperties properties;

    public JwtUtil(JwtProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(
                properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return generateToken(user, properties.accessTokenExpiration());
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, properties.refreshTokenExpiration());
    }

    public String generateTempToken(User user) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + properties.tempTokenExpiration());

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("phoneNumber", user.getPhoneNumber())
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
                .claim("phoneNumber", user.getPhoneNumber())
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

    public String getPhoneNumber(String token) {
        return getClaims(token).get("phoneNumber", String.class);
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
