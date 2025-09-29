package com.alpha_code.alpha_code_activity_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private int jwtExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private int refreshTokenExpirationMs;

    @Value("${jwt.reset-password-expiration-ms}")
    private int resetPasswordTokenExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Integer getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromJwt(String token) {
        return getAllClaims(token).getSubject();
    }

    public UUID getUserIdFromToken(String token) {
        String idStr = getAllClaims(token).get("id", String.class);
        return UUID.fromString(idStr);
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public String extractEmail(String token) {
        return getAllClaims(token).get("email", String.class);
    }
}
