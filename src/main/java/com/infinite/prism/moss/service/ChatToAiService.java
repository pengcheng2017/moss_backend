package com.infinite.prism.moss.service;

import com.infinite.prism.moss.entity.DTO.DifyApiChatDTO;
import com.infinite.prism.moss.entity.DTO.DifyApiChatResponse;
import com.infinite.prism.moss.entity.VO.ApiResultResponse;

public interface ChatToAiService {

    /**
     * 和AI 对话
     * @param request 聊天内容
     * @return AI的回复
     */
    ApiResultResponse<DifyApiChatResponse> chat(DifyApiChatDTO request);

    /**
     * 检查是否需要转人工
     * @param request 请求参数
     */
    Boolean askAIForCheckNeedToLive(DifyApiChatDTO request);

}
