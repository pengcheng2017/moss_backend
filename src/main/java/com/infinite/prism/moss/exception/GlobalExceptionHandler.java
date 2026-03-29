package com.infinite.prism.moss.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.infinite.prism.moss.entity.VO.ApiResultResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResultResponse<?> handleBusinessException(BusinessException e) {
        return ApiResultResponse.error(e.getMessage());
    }
}