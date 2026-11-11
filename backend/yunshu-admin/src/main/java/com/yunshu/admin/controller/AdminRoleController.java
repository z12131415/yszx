package com.yunshu.admin.controller;

import com.yunshu.common.annotation.LogOperation;
import com.yunshu.common.exception.ApiResponse;
import com.yunshu.system.domain.SysRole;
import com.yunshu.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping
    public ApiResponse<List<SysRole>> list() {
        return ApiResponse.ok(sysRoleService.findAll());
    }

    @PostMapping
    @LogOperation(actionType = "CREATE", targetType = "ROLE")
    public ApiResponse<SysRole> create(@RequestBody SysRole role) {
        return ApiResponse.ok(sysRoleService.create(role));
    }

    @PatchMapping("/{id}")
    @LogOperation(actionType = "UPDATE", targetType = "ROLE")
    public ApiResponse<SysRole> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        return ApiResponse.ok(sysRoleService.update(role));
    }

    @DeleteMapping("/{id}")
    @LogOperation(actionType = "DELETE", targetType = "ROLE")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sysRoleService.delete(id);
        return ApiResponse.ok();
    }
}
