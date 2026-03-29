package com.infinite.prism.moss.service;

import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.entity.VO.QuizResultVO;
import com.infinite.prism.moss.entity.request.QuizGetResultRequest;

public interface QuizService {

    ApiResultResponse<QuizResultVO> getAiResult(QuizGetResultRequest request);

}
