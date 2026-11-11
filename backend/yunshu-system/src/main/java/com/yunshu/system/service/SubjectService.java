package com.yunshu.system.service;

import com.yunshu.common.enums.ErrorCode;
import com.yunshu.common.exception.BusinessException;
import com.yunshu.system.domain.Catalog;
import com.yunshu.system.domain.Subject;
import com.yunshu.system.mapper.CatalogMapper;
import com.yunshu.system.mapper.SubjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectMapper subjectMapper;
    private final CatalogMapper catalogMapper;

    public List<Subject> findAll() {
        return subjectMapper.findAll();
    }

    public Subject findById(Long id) {
        Subject subject = subjectMapper.findById(id);
        if (subject == null) throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "学科不存在");
        return subject;
    }

    @Transactional
    public Subject create(Subject subject) {
        if (subjectMapper.findByName(subject.getName()) != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "学科名称已存在");
        }
        if (subject.getStatus() == null) subject.setStatus(1);
        if (subject.getSortOrder() == null) subject.setSortOrder(0);
        subject.setIsDeleted(0);
        subjectMapper.insert(subject);
        return subject;
    }

    @Transactional
    public Subject update(Subject subject) {
        Subject existing = findById(subject.getId());
        if (!existing.getName().equals(subject.getName())) {
            Subject dup = subjectMapper.findByName(subject.getName());
            if (dup != null) throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "学科名称已存在");
        }
        subjectMapper.update(subject);
        return subject;
    }

    @Transactional
    public void delete(Long id) {
        Subject subject = findById(id);
        long catalogCount = catalogMapper.countCatalogsBySubject(id);
        if (catalogCount > 0) {
            throw new BusinessException(ErrorCode.SUBJECT_HAS_CATALOGS);
        }
        subjectMapper.deleteById(id);
    }

    @Transactional
    public void toggleStatus(Long id, Integer status) {
        findById(id); // verify exists
        Subject subject = new Subject();
        subject.setId(id);
        subject.setStatus(status);
        subjectMapper.update(subject);
    }
}
