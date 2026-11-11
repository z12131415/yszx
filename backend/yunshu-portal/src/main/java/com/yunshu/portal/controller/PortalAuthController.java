package com.yunshu.portal.controller;

import com.yunshu.common.annotation.RateLimited;
import com.yunshu.common.enums.ErrorCode;
import com.yunshu.common.exception.ApiResponse;
import com.yunshu.common.util.JwtUtil;
import com.yunshu.common.util.PasswordUtils;
import com.yunshu.framework.cache.CacheService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/portal/auth")
@RequiredArgsConstructor
public class PortalAuthController {

    private final CacheService cacheService;
    private final JwtUtil jwtUtil;

    // 占位 Mapper，实际由 yunshu-portal 模块注入
    // 这里先创建最小可编译版本

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String password = body.get("password");
        String nickname = body.get("nickname");

        if (phone == null || phone.length() != 11 || !phone.matches("^1[3-9]\\d{9}$")) {
            return ApiResponse.error(ErrorCode.PHONE_INVALID);
        }
        if (password == null || password.length() < 8 || password.length() > 32
                || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            return ApiResponse.error(ErrorCode.PASSWORD_WEAK);
        }
        if (nickname == null || nickname.length() < 2 || nickname.length() > 20
                || nickname.matches("^\\d+$")) {
            return ApiResponse.error(ErrorCode.NICKNAME_INVALID);
        }

        // TODO: 通过学生账号服务注册
        Map<String, Object> data = new HashMap<>();
        data.put("message", "注册成功（框架已搭建，Mapper待接入）");
        return ApiResponse.ok(data);
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String password = body.get("password");

        if (phone == null || password == null) {
            return ApiResponse.error(ErrorCode.PARAM_ERROR.getCode(), "手机号和密码不能为空");
        }

        // TODO: 验证学生账号并签发 JWT
        Map<String, Object> data = new HashMap<>();
        data.put("message", "登录接口框架已搭建，Mapper待接入");
        return ApiResponse.ok(data);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("student_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/portal");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ApiResponse.ok();
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> getMe(@RequestHeader(value = "Cookie", required = false) String cookieHeader) {
        // TODO: 从 token 中解析用户信息
        Map<String, Object> data = new HashMap<>();
        data.put("message", "个人中心接口框架已搭建");
        return ApiResponse.ok(data);
    }
}
