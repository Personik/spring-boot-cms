package com.cms.security;

import com.cms.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtTokenService(
        @Value("${cms.jwt.secret}") String secret,
        @Value("${cms.jwt.expiration-ms}") long expirationMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        var builder = Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", user.getId())
            .claim("role", user.getRole().name())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(signingKey, SignatureAlgorithm.HS256);

        if (user.getTenant() != null) {
            builder.claim("tenantId", user.getTenant().getId());
        }

        return builder.compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}

