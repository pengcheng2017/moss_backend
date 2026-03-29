package com.infinite.prism.moss.entity.request;

import lombok.Data;

import java.util.Map;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/6 15:12
 */
@Data
public class QuizGetResultRequest {

    public String type;

    public Long quizId;

    public Map<Integer, Integer> answerMap;

}
