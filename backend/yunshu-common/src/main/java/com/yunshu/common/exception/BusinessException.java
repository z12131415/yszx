package com.yunshu.common.exception;

import com.yunshu.common.enums.ErrorCode;
import lombok.Getter;

/**
 * 业务异常基类
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;
    private final String message;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.code = errorCode.getCode();
        this.message = detailMessage;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
