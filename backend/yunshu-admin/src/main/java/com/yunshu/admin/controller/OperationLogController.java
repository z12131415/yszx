package com.yunshu.admin.controller;

import com.yunshu.common.annotation.LogOperation;
import com.yunshu.common.exception.ApiResponse;
import com.yunshu.system.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogMapper operationLogMapper;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String keyword) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate + "T00:00:00") : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate + "T23:59:59") : null;
        List<?> logs = operationLogMapper.findList(userId, actionType, targetType, start, end, keyword);
        return ApiResponse.ok((List<Map<String, Object>>) (List<?>) logs);
    }

    @DeleteMapping("/clear")
    @LogOperation(actionType = "DELETE", targetType = "LOG")
    public ApiResponse<Map<String, Object>> clear() {
        int count = operationLogMapper.clearAll();
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("clearedCount", count);
        return ApiResponse.ok(result);
    }
}
