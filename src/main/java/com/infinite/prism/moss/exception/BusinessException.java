package com.infinite.prism.moss.exception;


import com.infinite.prism.moss.constants.enums.ErrorCode;

import java.util.HashMap;
import java.util.Map;

public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> data;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = new HashMap<>();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.data = new HashMap<>();
    }

    public BusinessException(ErrorCode errorCode, Map<String, Object> data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = data;
    }
}