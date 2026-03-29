package com.infinite.prism.moss.service.impl;

import com.infinite.prism.moss.entity.request.InfoGenRequest;
import com.infinite.prism.moss.properties.DifyApiProperties;
import com.infinite.prism.moss.service.AiAgentService;
import com.infinite.prism.moss.service.ChatToAiService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/10 17:22
 */
@Slf4j
@Service
public class AiAgentServiceImpl implements AiAgentService {

    @Resource
    private ChatToAiService chatToAiService;

    @Resource
    private DifyApiProperties properties;

    @Override
    public String infoGate(InfoGenRequest request) {
        // 如果信息未收集完成
        // 如果信息收集完成
        // 收集完成 催付款

        return "";
    }
}
