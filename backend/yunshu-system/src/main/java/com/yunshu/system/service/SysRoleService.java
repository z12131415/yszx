package com.yunshu.system.service;

import com.alibaba.fastjson2.JSON;
import com.yunshu.common.enums.ErrorCode;
import com.yunshu.common.exception.BusinessException;
import com.yunshu.system.domain.SysRole;
import com.yunshu.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SysRoleService {

    private static final Set<String> RESERVED_CODES = Set.of("super_admin", "operator", "content_reviewer");

    private final SysRoleMapper sysRoleMapper;

    public List<SysRole> findAll() {
        return sysRoleMapper.findAll();
    }

    public SysRole findById(Long id) {
        SysRole role = sysRoleMapper.findById(id);
        if (role == null) throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "角色不存在");
        return role;
    }

    public SysRole create(SysRole role) {
        if (RESERVED_CODES.contains(role.getCode().toLowerCase())) {
            throw new BusinessException(ErrorCode.RESERVED_ROLE_CODE);
        }
        if (sysRoleMapper.findByCode(role.getCode()) != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "角色编码已存在");
        }
        // 初始化 permissions
        if (role.getPermissions() == null) {
            role.setPermissions(new ArrayList<>());
        }
        sysRoleMapper.insert(role);
        return role;
    }

    public SysRole update(SysRole role) {
        SysRole existing = findById(role.getId());
        if (existing == null) throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "角色不存在");
        if (RESERVED_CODES.contains(role.getCode().toLowerCase()) &&
                !existing.getCode().equalsIgnoreCase(role.getCode())) {
            throw new BusinessException(ErrorCode.RESERVED_ROLE_CODE);
        }
        if (role.getPermissions() == null) role.setPermissions(existing.getPermissions());
        sysRoleMapper.update(role);
        return role;
    }

    public void delete(Long id) {
        SysRole role = findById(id);
        int userCount = sysRoleMapper.countUsersByRoleId(id);
        if (userCount > 0) {
            throw new BusinessException(ErrorCode.ROLE_HAS_USERS);
        }
        sysRoleMapper.deleteById(id);
    }
}
