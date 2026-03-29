package com.infinite.prism.moss.service.impl;

import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.entity.VO.QuizResultVO;
import com.infinite.prism.moss.entity.request.QuizGetResultRequest;
import com.infinite.prism.moss.service.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/6 15:21
 */
@Slf4j
@Service("soulPortraitQuiz")
public class SoulPortraitQuizServiceImpl implements QuizService {
    @Override
    public ApiResultResponse<QuizResultVO> getAiResult(QuizGetResultRequest request) {
        log.info("soul portrait quiz");
        return null;
    }
}
