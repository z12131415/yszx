package com.yunshu.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 */
@Component
public class JwtUtil {

    private final SecretKey studentSecret;
    private final SecretKey adminSecret;
    private final long studentExpirationMs;
    private final long adminExpirationMs;

    public JwtUtil(
            @Value("${yunshu.jwt.student.secret}") String studentSecret,
            @Value("${yunshu.jwt.student.expiration-ms}") long studentExpirationMs,
            @Value("${yunshu.jwt.admin.secret}") String adminSecret,
            @Value("${yunshu.jwt.admin.expiration-ms}") long adminExpirationMs) {
        this.studentSecret = Keys.hmacShaKeyFor(studentSecret.getBytes(StandardCharsets.UTF_8));
        this.adminSecret = Keys.hmacShaKeyFor(adminSecret.getBytes(StandardCharsets.UTF_8));
        this.studentExpirationMs = studentExpirationMs;
        this.adminExpirationMs = adminExpirationMs;
    }

    public String generateStudentToken(long userId, String nickname) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", "student")
                .claim("nickname", nickname)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + studentExpirationMs))
                .signWith(studentSecret)
                .compact();
    }

    public String generateAdminToken(long userId, String username, String roleCode, java.util.List<String> permissions) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", "admin")
                .claim("username", username)
                .claim("roleCode", roleCode)
                .claim("permissions", permissions)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + adminExpirationMs))
                .signWith(adminSecret)
                .compact();
    }

    public Long parseUserId(String token, boolean isAdmin) {
        SecretKey secret = isAdmin ? adminSecret : studentSecret;
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenExpired(String token, boolean isAdmin) {
        SecretKey secret = isAdmin ? adminSecret : studentSecret;
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public String getJwtId(String token, boolean isAdmin) {
        SecretKey secret = isAdmin ? adminSecret : studentSecret;
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getId();
    }

    @SuppressWarnings("unchecked")
    public <T> T getClaim(String token, boolean isAdmin, String claimName, Class<T> type) {
        SecretKey secret = isAdmin ? adminSecret : studentSecret;
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(claimName, type);
    }
}
