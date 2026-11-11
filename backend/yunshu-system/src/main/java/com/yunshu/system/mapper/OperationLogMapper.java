package com.yunshu.system.mapper;

import com.yunshu.system.domain.OperationLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OperationLogMapper {

    @Insert("INSERT INTO operation_log (user_id, username, action_type, target_type, target_id, detail, ip_address, created_at) " +
            "VALUES (#{userId}, #{username}, #{actionType}, #{targetType}, #{targetId}, #{detail}, #{ipAddress}, #{createdAt})")
    void insert(OperationLog log);

    @Select("SELECT * FROM operation_log ol " +
            "WHERE 1=1 " +
            "<if test='userId != null'>AND ol.user_id = #{userId}</if>" +
            "<if test='actionType != null and actionType != \"\"'>AND ol.action_type = #{actionType}</if>" +
            "<if test='targetType != null and targetType != \"\"'>AND ol.target_type = #{targetType}</if>" +
            "<if test='startDate != null'>AND ol.created_at &gt;= #{startDate}</if>" +
            "<if test='endDate != null'>AND ol.created_at &lt;= #{endDate}</if>" +
            "<if test='keyword != null and keyword != \"\"'>AND (ol.detail LIKE CONCAT('%', #{keyword}, '%') OR ol.username LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            "ORDER BY ol.created_at DESC")
    List<OperationLog> findList(@Param("userId") Long userId,
                                @Param("actionType") String actionType,
                                @Param("targetType") String targetType,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate,
                                @Param("keyword") String keyword);

    @Select("SELECT COUNT(*) FROM operation_log")
    long countAll();

    @Delete("DELETE FROM operation_log")
    int clearAll();
}
