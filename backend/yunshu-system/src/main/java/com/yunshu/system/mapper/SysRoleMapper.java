package com.yunshu.system.mapper;

import com.yunshu.system.domain.SysRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    @Select("SELECT * FROM sys_role WHERE code = #{code} AND is_deleted = 0")
    SysRole findByCode(@Param("code") String code);

    @Select("SELECT * FROM sys_role WHERE id = #{id} AND is_deleted = 0")
    SysRole findById(@Param("id") Long id);

    @Select("SELECT * FROM sys_role WHERE is_deleted = 0 ORDER BY id")
    List<SysRole> findAll();

    @Insert("INSERT INTO sys_role (code, name, description, permissions) VALUES (#{code}, #{name}, #{description}, #{permissions, typeHandler=com.yunshu.common.type.JsonTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SysRole role);

    @Update("UPDATE sys_role SET name = #{name}, description = #{description}, permissions = #{permissions, typeHandler=com.yunshu.common.type.JsonTypeHandler}, updated_at = NOW() WHERE id = #{id}")
    int update(SysRole role);

    @Update("UPDATE sys_role SET is_deleted = 1, updated_at = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM admin_user WHERE role_id = #{roleId} AND is_deleted = 0")
    int countUsersByRoleId(@Param("roleId") Long roleId);
}
