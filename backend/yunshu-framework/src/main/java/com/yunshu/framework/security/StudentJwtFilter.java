package com.yunshu.framework.security;

import com.yunshu.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * 学员端 JWT 过滤器 — 从 Cookie 中读取 student_token 并认证
 */
@Component
@RequiredArgsConstructor
public class StudentJwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final String COOKIE_NAME = "student_token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractTokenFromCookie(request, COOKIE_NAME);

        if (StringUtils.hasText(token) && !jwtUtil.isTokenExpired(token, false)) {
            try {
                Long userId = jwtUtil.parseUserId(token, false);
                String nickname = jwtUtil.getClaim(token, false, "nickname", String.class);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                authentication.setDetails(nickname);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ignored) {
                // Token 无效，忽略
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
