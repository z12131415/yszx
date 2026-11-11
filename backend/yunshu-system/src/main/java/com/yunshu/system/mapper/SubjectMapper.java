package com.yunshu.system.mapper;

import com.yunshu.system.domain.Subject;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SubjectMapper {

    @Select("SELECT * FROM subject WHERE is_deleted = 0 ORDER BY sort_order ASC, id ASC")
    List<Subject> findAll();

    @Select("SELECT * FROM subject WHERE id = #{id} AND is_deleted = 0")
    Subject findById(@Param("id") Long id);

    @Select("SELECT * FROM subject WHERE name = #{name} AND is_deleted = 0")
    Subject findByName(@Param("name") String name);

    @Insert("INSERT INTO subject (name, icon, description, sort_order, color, status) " +
            "VALUES (#{name}, #{icon}, #{description}, #{sortOrder}, #{color}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Subject subject);

    @Update("UPDATE subject SET icon = #{icon}, description = #{description}, sort_order = #{sortOrder}, " +
            "color = #{color}, status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int update(Subject subject);

    @Update("UPDATE subject SET is_deleted = 1, updated_at = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE subject SET sort_order = #{sortOrder}, updated_at = NOW() WHERE id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>")
    int batchUpdateSortOrder(@Param("ids") List<Long> ids, @Param("sortOrders") List<Integer> sortOrders);
}
