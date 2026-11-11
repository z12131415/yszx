package com.yunshu.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应体
 *
 * @param <T> 数据类型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;
    private java.util.List<FieldError> fieldErrors;

    public ApiResponse() {}

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(0, "success", null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "success", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String detail) {
        return new ApiResponse<>(errorCode.getCode(), detail, null);
    }

    @Data
    public static class FieldError {
        private String field;
        private String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
