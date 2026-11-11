package com.yunshu.portal.controller;

import com.yunshu.common.annotation.RateLimited;
import com.yunshu.common.enums.ErrorCode;
import com.yunshu.common.exception.BusinessException;
import com.yunshu.common.exception.ApiResponse;
import com.yunshu.common.util.JwtUtil;
import com.yunshu.common.util.PasswordUtils;
import com.yunshu.framework.cache.CacheService;
import com.yunshu.system.domain.AdminUser;
import com.yunshu.system.domain.SysRole;
import com.yunshu.system.mapper.AdminUserMapper;
import com.yunshu.system.mapper.SysRoleMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminUserMapper adminUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final JwtUtil jwtUtil;
    private final CacheService cacheService;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return ApiResponse.error(ErrorCode.CREDENTIALS_INVALID);
        }

        // 登录锁定检查
        String lockKey = "ratelimit:login:block:" + username;
        Long failCount = cacheService.getCounter(lockKey);
        if (failCount != null && failCount >= 5) {
            return ApiResponse.error(ErrorCode.TOO_MANY_REQUESTS.getCode(), "账号已锁定，请5分钟后重试");
        }

        AdminUser user = adminUserMapper.findByUsername(username);
        if (user == null) {
            incrementLoginFail(username);
            return ApiResponse.error(ErrorCode.CREDENTIALS_INVALID);
        }
        if (user.getStatus() != 1) {
            return ApiResponse.error(ErrorCode.ACCOUNT_DISABLED);
        }
        if (!new BCryptPasswordEncoder().matches(password, user.getPasswordHash())) {
            incrementLoginFail(username);
            return ApiResponse.error(ErrorCode.CREDENTIALS_INVALID);
        }

        // 清除失败计数，更新登录时间
        cacheService.delete(lockKey);
        adminUserMapper.updateLastLogin(user.getId());

        SysRole role = sysRoleMapper.findById(user.getRoleId());
        List<String> permissions = role != null && role.getPermissions() != null
                ? role.getPermissions() : List.of();

        String token = jwtUtil.generateAdminToken(user.getId(), user.getUsername(),
                role != null ? role.getCode() : "", permissions);

        // 设置 HttpOnly Cookie
        Cookie cookie = new Cookie("admin_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // dev only; set true in prod behind HTTPS
        cookie.setPath("/admin");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("roleCode", role != null ? role.getCode() : "");
        data.put("roleName", role != null ? role.getName() : "");
        data.put("permissions", permissions);
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("needChangePassword", false); // 后续可加 need_change_password 字段

        return ApiResponse.ok(data);
    }

    private void incrementLoginFail(String username) {
        cacheService.hIncrement("ratelimit:login:block:" + username, "fails", 300);
    }
}
