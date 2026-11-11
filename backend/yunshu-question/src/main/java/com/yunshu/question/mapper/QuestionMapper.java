package com.yunshu.question.mapper;

import com.yunshu.question.domain.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface QuestionMapper {

    @Select("SELECT * FROM question WHERE catalog_id = #{catalogId} AND status = 'published' AND is_deleted = 0 " +
            "<if test='type != null'>AND type = #{type}</if>" +
            "<if test='difficulty != null'>AND difficulty = #{difficulty}</if>" +
            "<if test='keyword != null and keyword != \"\"'>AND MATCH(content) AGAINST(#{keyword} IN BOOLEAN MODE)</if>" +
            "ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<Question> findList(@Param("catalogId") Long catalogId,
                            @Param("type") String type,
                            @Param("difficulty") String difficulty,
                            @Param("keyword") String keyword,
                            @Param("offset") int offset,
                            @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM question WHERE catalog_id = #{catalogId} AND status = 'published' AND is_deleted = 0 " +
            "<if test='type != null'>AND type = #{type}</if>" +
            "<if test='difficulty != null'>AND difficulty = #{difficulty}</if>" +
            "<if test='keyword != null and keyword != \"\"'>AND MATCH(content) AGAINST(#{keyword} IN BOOLEAN MODE)</if>")
    long countList(@Param("catalogId") Long catalogId,
                   @Param("type") String type,
                   @Param("difficulty") String difficulty,
                   @Param("keyword") String keyword);

    @Select("SELECT * FROM question WHERE id = #{id} AND is_deleted = 0")
    Question findById(@Param("id") Long id);

    @Insert("INSERT INTO question (catalog_id, content, type, difficulty, options, answer, analysis, tags, status, view_count, answer_count, correct_count) " +
            "VALUES (#{catalogId}, #{content}, #{type}, #{difficulty}, #{options, typeHandler=com.yunshu.common.type.JsonTypeHandler}, " +
            "#{answer}, #{analysis}, #{tags, typeHandler=com.yunshu.common.type.JsonTypeHandler}, #{status}, #{viewCount}, #{answerCount}, #{correctCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Question question);

    @Update("UPDATE question SET content = #{content}, type = #{type}, difficulty = #{difficulty}, " +
            "options = #{options, typeHandler=com.yunshu.common.type.JsonTypeHandler}, answer = #{answer}, " +
            "analysis = #{analysis}, tags = #{tags, typeHandler=com.yunshu.common.type.JsonTypeHandler}, updated_at = NOW() " +
            "WHERE id = #{id} AND is_deleted = 0")
    int update(Question question);

    @Update("UPDATE question SET is_deleted = 1, updated_at = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE question SET status = #{status}, reviewed_by = #{reviewedBy}, reviewed_at = NOW(), " +
            "review_comment = #{reviewComment}, updated_at = NOW() WHERE id = #{id}")
    int review(@Param("id") Long id, @Param("status") String status,
               @Param("reviewedBy") Long reviewedBy, @Param("reviewComment") String reviewComment);
}
