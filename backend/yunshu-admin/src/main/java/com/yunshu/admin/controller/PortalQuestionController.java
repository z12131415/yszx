package com.yunshu.admin.controller;

import com.yunshu.question.service.QuestionService;
import com.yunshu.system.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portal/questions")
@RequiredArgsConstructor
public class PortalQuestionController {

    private final QuestionService questionService;
    private final CatalogService catalogService;

    @GetMapping
    public Object list(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long catalogId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        // TODO: 实现学员端题目列表（含答题统计聚合）
        return null;
    }

    @GetMapping("/{id}")
    public Object detail(@PathVariable Long id) {
        return questionService.findById(id);
    }
}
