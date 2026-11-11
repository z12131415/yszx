package com.yunshu.system.service;

import com.yunshu.common.enums.ErrorCode;
import com.yunshu.common.exception.BusinessException;
import com.yunshu.system.domain.Catalog;
import com.yunshu.system.mapper.CatalogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CatalogMapper catalogMapper;

    public List<Catalog> findBySubjectId(Long subjectId) {
        return catalogMapper.findBySubjectId(subjectId);
    }

    public Catalog findById(Long id) {
        Catalog catalog = catalogMapper.findById(id);
        if (catalog == null) throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "阶段不存在");
        return catalog;
    }

    @Transactional
    public Catalog create(Catalog catalog, Long subjectId) {
        Catalog existing = catalogMapper.findBySubjectAndName(subjectId, catalog.getName());
        if (existing != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "同阶段名称已存在");
        }
        catalog.setSubjectId(subjectId);
        if (catalog.getStatus() == null) catalog.setStatus(1);
        if (catalog.getSortOrder() == null) catalog.setSortOrder(0);
        catalog.setIsDeleted(0);
        catalogMapper.insert(catalog);
        return catalog;
    }

    @Transactional
    public Catalog update(Catalog catalog) {
        findById(catalog.getId());
        catalogMapper.update(catalog);
        return catalog;
    }

    @Transactional
    public void delete(Long id) {
        Catalog catalog = findById(id);
        long questionCount = catalogMapper.countPublishedQuestionsByCatalog(id);
        if (questionCount > 0) {
            throw new BusinessException(ErrorCode.CATALOG_HAS_PUBLISHED_QUESTIONS);
        }
        catalogMapper.deleteById(id);
    }
}
