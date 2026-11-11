package com.yunshu.admin.controller;

import com.yunshu.common.annotation.LogOperation;
import com.yunshu.common.exception.ApiResponse;
import com.yunshu.system.domain.AdminUser;
import com.yunshu.system.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ApiResponse<List<AdminUser>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Integer status) {
        // TODO: 接入分页
        return ApiResponse.ok(List.of());
    }

    @PostMapping
    @LogOperation(actionType = "CREATE", targetType = "USER")
    public ApiResponse<AdminUser> create(@RequestBody Map<String, String> body) {
        AdminUser user = new AdminUser();
        user.setUsername(body.get("username"));
        user.setPhone(body.get("phone"));
        user.setEmail(body.get("email"));
        user.setRoleId(Long.valueOf(body.get("roleId")));
        String rawPassword = body.get("password");
        adminUserService.create(user, rawPassword);
        user.setId(1L); // stub
        return ApiResponse.ok(user);
    }

    @PatchMapping("/{id}")
    @LogOperation(actionType = "UPDATE", targetType = "USER")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        // TODO: 实现更新逻辑
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/reset-password")
    @LogOperation(actionType = "UPDATE", targetType = "USER")
    public ApiResponse<Map<String, Object>> resetPassword(@PathVariable Long id,
                                                          @RequestBody Map<String, String> body) {
        adminUserService.resetPassword(id, body.get("newPassword"));
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("message", "密码已重置，请以新密码登录");
        result.put("tempPasswordShown", false);
        return ApiResponse.ok(result);
    }

    @DeleteMapping("/batch")
    @LogOperation(actionType = "DELETE", targetType = "USER")
    public ApiResponse<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        adminUserService.batchDelete(body.get("ids"));
        return ApiResponse.ok();
    }
}
