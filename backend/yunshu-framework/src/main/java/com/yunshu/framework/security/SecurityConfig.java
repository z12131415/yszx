package com.yunshu.framework.security;

import com.yunshu.common.enums.ErrorCode;
import com.yunshu.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 配置 — 双路径认证（学员端 / 管理端）
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final StudentJwtFilter studentJwtFilter;
    private final AdminJwtFilter adminJwtFilter;

    /** 学员端安全过滤链 */
    @Bean
    public SecurityFilterChain portalFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/portal/**")
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公开接口
                .requestMatchers(HttpMethod.POST, "/api/portal/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/portal/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/portal/auth/login-sms").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/portal/auth/send-code").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/portal/auth/forgot-password").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/portal/questions").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/portal/questions/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/portal/catalogs/**/questions").permitAll()
                // 需认证
                .anyRequest().authenticated()
            )
            .addFilterBefore(studentJwtFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":40100,\"message\":\"未登录或Token失效\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":40301,\"message\":\"无权执行此操作\"}");
                })
            );
        return http.build();
    }

    /** 管理端安全过滤链 */
    @Bean
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/admin/**")
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公开接口
                .requestMatchers(HttpMethod.POST, "/api/admin/auth/login").permitAll()
                // 需要认证的其他接口
                .anyRequest().authenticated()
            )
            .addFilterBefore(adminJwtFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":40100,\"message\":\"未登录或Token失效\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":40301,\"message\":\"无权执行此操作\"}");
                })
            );
        return http.build();
    }

    /** CORS 配置 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:80"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
