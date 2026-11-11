package com.yunshu.system.mapper;

import com.yunshu.system.domain.Catalog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CatalogMapper {

    @Select("SELECT * FROM catalog WHERE subject_id = #{subjectId} AND is_deleted = 0 ORDER BY sort_order ASC")
    List<Catalog> findBySubjectId(@Param("subjectId") Long subjectId);

    @Select("SELECT * FROM catalog WHERE id = #{id} AND is_deleted = 0")
    Catalog findById(@Param("id") Long id);

    @Select("SELECT * FROM catalog WHERE subject_id = #{subjectId} AND name = #{name} AND is_deleted = 0")
    Catalog findBySubjectAndName(@Param("subjectId") Long subjectId, @Param("name") String name);

    @Insert("INSERT INTO catalog (subject_id, name, icon, sort_order, status) VALUES (#{subjectId}, #{name}, #{icon}, #{sortOrder}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Catalog catalog);

    @Update("UPDATE catalog SET name = #{name}, icon = #{icon}, sort_order = #{sortOrder}, status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int update(Catalog catalog);

    @Update("UPDATE catalog SET is_deleted = 1, updated_at = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM question q WHERE q.catalog_id = #{catalogId} AND q.status = 'published' AND q.is_deleted = 0")
    long countPublishedQuestionsByCatalog(@Param("catalogId") Long catalogId);

    @Select("SELECT COUNT(*) FROM subject s WHERE s.id = #{subjectId} AND s.is_deleted = 0")
    long countCatalogsBySubject(@Param("subjectId") Long subjectId);
}
