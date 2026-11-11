package com.yunshu.common.exception;

import com.yunshu.common.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常 code={}, message={}", e.getCode(), e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /** AI 服务异常 */
    @ExceptionHandler(AiServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleAiException(AiServiceException e) {
        log.error("AI 服务异常", e);
        return ApiResponse.error(ErrorCode.AI_SERVICE_ERROR);
    }

    /** 参数校验异常（@Valid） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return ApiResponse.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /** 绑定异常（查询参数） */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getField() + ": " + fieldError.getDefaultMessage() : "参数绑定失败";
        log.warn("参数绑定失败: {}", message);
        return ApiResponse.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /** 权限不足 */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleAccessDenied(org.springframework.security.access.AccessDeniedException e) {
        log.warn("权限不足");
        return ApiResponse.error(ErrorCode.ACCESS_DENIED);
    }

    /** 认证失败 */
    @ExceptionHandler(org.springframework.security.authentication.AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleAuthException(org.springframework.security.authentication.AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.UNAUTHORIZED);
    }

    /** 未捕获异常 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("服务端内部错误", e);
        return ApiResponse.error(ErrorCode.INTERNAL_ERROR);
    }
}
