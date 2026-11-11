package com.yunshu.question.service;

import com.yunshu.common.enums.ErrorCode;
import com.yunshu.common.exception.BusinessException;
import com.yunshu.question.domain.Question;
import com.yunshu.question.mapper.QuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionMapper questionMapper;

    public List<Question> findPublished(long catalogId, String type, String difficulty, String keyword, int offset, int pageSize) {
        return questionMapper.findList(catalogId, type, difficulty, keyword, offset, pageSize);
    }

    public long countPublished(long catalogId, String type, String difficulty, String keyword) {
        return questionMapper.countList(catalogId, type, difficulty, keyword);
    }

    public Question findById(Long id) {
        Question q = questionMapper.findById(id);
        if (q == null) throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "题目不存在");
        return q;
    }

    public Question create(Question q) {
        if (q.getStatus() == null) q.setStatus("draft");
        if (q.getViewCount() == null) q.setViewCount(0);
        if (q.getAnswerCount() == null) q.setAnswerCount(0);
        if (q.getCorrectCount() == null) q.setCorrectCount(0);
        questionMapper.insert(q);
        return q;
    }

    public void update(Question q) {
        Question existing = findById(q.getId());
        if ("published".equals(existing.getStatus())) {
            throw new BusinessException(ErrorCode.PUBLISHED_QUESTION_EDIT_FORBIDDEN);
        }
        questionMapper.update(q);
    }

    public void review(Long id, String action, Long reviewedBy, String comment) {
        Question q = findById(id);
        if (!"pending_review".equals(q.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION_STATUS);
        }
        String newStatus = "approve".equals(action) ? "published" : "rejected";
        questionMapper.review(id, newStatus, reviewedBy, "reject".equals(action) ? comment : null);
    }
}
