package com.infinite.prism.moss.entity.VO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author liao.peng
 * @since 2025/4/12 22:17
 */
@Data
public class ApiResultResponse<T> implements Serializable {

    /**
     * "状态码。200表示成功"
     */
    private Integer code;

    /**
     * "结果集"
     */
    private List<T> datas;

    /**
     * 单个结果
     */
    private T data;

    private String message;

    public static <T> ApiResultResponse<T> ok(String message, T t) {
        ApiResultResponse<T> response = new ApiResultResponse<>();
        response.setData(t);
        response.setMessage(message);
        response.setCode(200);
        return response;
    }

    public static <T> ApiResultResponse<T> ok(String message) {
        ApiResultResponse<T> response = new ApiResultResponse<>();
        response.setMessage(message);
        response.setCode(200);
        return response;
    }

    public static <T> ApiResultResponse<T> ok(T t) {
        ApiResultResponse<T> response = new ApiResultResponse<>();
        response.setMessage("ok");
        response.setData(t);
        response.setCode(200);
        return response;
    }

    public static <T> ApiResultResponse<T> error(Integer code, String message) {
        ApiResultResponse<T> response = new ApiResultResponse<>();
        response.setMessage(message);
        response.setCode(code);
        return response;
    }

    public static <T> ApiResultResponse<T> error(String message) {
        ApiResultResponse<T> response = new ApiResultResponse<>();
        response.setMessage(message);
        response.setCode(500);
        return response;
    }

    public static <T> ApiResultResponse<T> error(String message, T t) {
        ApiResultResponse<T> response = new ApiResultResponse<>();
        response.setMessage(message);
        response.setData(t);
        response.setCode(500);
        return response;
    }

}