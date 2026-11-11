package com.yunshu.system.controller;

import com.yunshu.common.annotation.LogOperation;
import com.yunshu.common.exception.ApiResponse;
import com.yunshu.system.domain.Subject;
import com.yunshu.system.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ApiResponse<List<Subject>> list() {
        return ApiResponse.ok(subjectService.findAll());
    }

    @PostMapping
    @LogOperation(actionType = "CREATE", targetType = "SUBJECT")
    public ApiResponse<Subject> create(@RequestBody Subject subject) {
        return ApiResponse.ok(subjectService.create(subject));
    }

    @PatchMapping("/{id}")
    @LogOperation(actionType = "UPDATE", targetType = "SUBJECT")
    public ApiResponse<Subject> update(@PathVariable Long id, @RequestBody Subject subject) {
        subject.setId(id);
        return ApiResponse.ok(subjectService.update(subject));
    }

    @DeleteMapping("/{id}")
    @LogOperation(actionType = "DELETE", targetType = "SUBJECT")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/status")
    @LogOperation(actionType = "UPDATE", targetType = "SUBJECT")
    public ApiResponse<Void> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        subjectService.toggleStatus(id, body.get("status"));
        return ApiResponse.ok();
    }
}
