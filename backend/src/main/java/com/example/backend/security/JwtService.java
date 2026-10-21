package com.example.backend.security;

import com.example.backend.exception.ApiException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtService {

    private final SecretKey key;
    private final Duration expiresIn;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expires-in}") Duration expiresIn) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiresIn = expiresIn;
    }

    public String sign(String subject) {
        Date now = new Date();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiresIn.toMillis()))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    // prima vrijednost zaglavlja "Bearer <token>", vraća ko je prijavljen
    public String verify(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw ApiException.unauthorized("Niste prijavljeni");
        }
        try {
            return Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(header.substring(7))
                    .getPayload().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw ApiException.unauthorized("Prijava je istekla, prijavite se ponovo");
        }
    }
}
