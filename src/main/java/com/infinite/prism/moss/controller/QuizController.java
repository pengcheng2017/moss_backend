package com.infinite.prism.moss.controller;

import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.entity.VO.QuizResultVO;
import com.infinite.prism.moss.entity.request.QuizGetResultRequest;
import com.infinite.prism.moss.service.QuizService;
import com.infinite.prism.moss.service.factory.QuizServiceFactory;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/6 15:02
 */
@RestController
public class QuizController {

    private final QuizServiceFactory quizServiceFactory;

    public QuizController(QuizServiceFactory quizServiceFactory) {
        this.quizServiceFactory = quizServiceFactory;
    }

    public ApiResultResponse<QuizResultVO> getAiResult(QuizGetResultRequest request) {
        QuizService quizService = quizServiceFactory.getQuizService(request.getType());
        return quizService.getAiResult(request);
    }

}
