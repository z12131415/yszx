package com.yunshu.framework.security;

import com.yunshu.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端 JWT 过滤器 — 从 Cookie 中读取 admin_token 并认证
 */
@Component
@RequiredArgsConstructor
public class AdminJwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final String COOKIE_NAME = "admin_token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractTokenFromCookie(request, COOKIE_NAME);

        if (StringUtils.hasText(token) && !jwtUtil.isTokenExpired(token, true)) {
            try {
                Long userId = jwtUtil.parseUserId(token, true);
                String username = jwtUtil.getClaim(token, true, "username", String.class);
                String roleCode = jwtUtil.getClaim(token, true, "roleCode", String.class);
                @SuppressWarnings("unchecked")
                List<String> permissions = jwtUtil.getClaim(token, true, "permissions", List.class);

                List<SimpleGrantedAuthority> authorities = permissions != null
                        ? permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                        : List.of();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authentication.setDetails(username);
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
