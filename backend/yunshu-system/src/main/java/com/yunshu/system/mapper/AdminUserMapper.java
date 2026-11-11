package com.yunshu.system.mapper;

import com.yunshu.system.domain.AdminUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminUserMapper {

    @Select("SELECT * FROM admin_user WHERE username = #{username} AND is_deleted = 0")
    AdminUser findByUsername(@Param("username") String username);

    @Select("SELECT * FROM admin_user WHERE id = #{id} AND is_deleted = 0")
    AdminUser findById(@Param("id") Long id);

    @Insert("INSERT INTO admin_user (username, phone, email, password_hash, role_id, status) " +
            "VALUES (#{username}, #{phone}, #{email}, #{passwordHash}, #{roleId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AdminUser user);

    @Update("UPDATE admin_user SET password_hash = #{passwordHash}, updated_at = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);

    @Update("UPDATE admin_user SET last_login_at = NOW(), updated_at = NOW() WHERE id = #{id}")
    int updateLastLogin(@Param("id") Long id);

    @Update("UPDATE admin_user SET status = #{status}, updated_at = NOW() WHERE id = #{id} AND is_deleted = 0")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE admin_user SET is_deleted = 1, updated_at = NOW() WHERE id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>")
    int batchDelete(@Param("ids") List<Long> ids);

    @Select("SELECT * FROM admin_user au " +
            "LEFT JOIN sys_role sr ON au.role_id = sr.id " +
            "WHERE au.is_deleted = 0 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (au.username LIKE CONCAT('%', #{keyword}, '%') OR au.phone LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='roleId != null'>AND au.role_id = #{roleId}</if>" +
            "<if test='status != null'>AND au.status = #{status}</if>" +
            "ORDER BY au.created_at DESC")
    List<AdminUser> findList(@Param("keyword") String keyword,
                             @Param("roleId") Long roleId,
                             @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM admin_user WHERE is_deleted = 0")
    long countAll();
}
