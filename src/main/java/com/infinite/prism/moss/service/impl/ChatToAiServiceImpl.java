package com.infinite.prism.moss.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.infinite.prism.moss.entity.DO.Bot;
import com.infinite.prism.moss.entity.DTO.DifyApiChatDTO;
import com.infinite.prism.moss.entity.DTO.DifyApiChatResponse;
import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.entity.VO.UserVO;
import com.infinite.prism.moss.properties.DifyApiProperties;
import com.infinite.prism.moss.service.BotService;
import com.infinite.prism.moss.service.ChatToAiService;
import com.infinite.prism.moss.utils.HttpClientUtil;
import com.infinite.prism.moss.utils.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/10 17:32
 */
@Slf4j
@Service
public class ChatToAiServiceImpl implements ChatToAiService {

    public static final String CHAT_MESSAGE = "/chat-messages";

    @Resource
    private BotService botService;

    @Resource
    private DifyApiProperties properties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResultResponse<DifyApiChatResponse> chat(DifyApiChatDTO request) {
        // 参数校验
        validateDifyConfig();

        UserVO userVO = UserContext.getUserVO();
        if (userVO == null) {
            throw new RuntimeException("用户未登录");
        }
        try {
            // 获取机器人配置
            Bot bot = getBotConfig();
            validateBotConfig(bot);
            request.setQuery(request.getQuery());
            // 调用Dify API
            DifyApiChatResponse response = callDifyApi(request, bot);
            if (response != null && response.getAnswer() != null) {
                response.setAnswer(response.getAnswer().replace("*", ""));
            }
            log.info("dify 聊天结果: {}", JSONObject.toJSONString(response));
            return ApiResultResponse.ok("ok", response);
        } catch (Exception e) {
            log.error("聊天服务异常", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean askAIForCheckNeedToLive(DifyApiChatDTO request) {
        return null;
    }

    /**
     * 参数校验方法
     */
    private void validateDifyConfig() {
        if (properties.getBaseurl() == null) {
            throw new RuntimeException("请先配置 dify base url");
        }
    }

    private Bot getBotConfig() {
        Bot bot = botService.getBotByUserId();
        if (bot == null) {
            bot = botService.getById(-1L);
        }
        return bot;
    }

    private void validateBotConfig(Bot bot) {
        if (bot.getDifyApiKey() == null) {
            throw new RuntimeException("请先配置 dify api 密钥");
        }
    }


    private DifyApiChatResponse callDifyApi(DifyApiChatDTO request, Bot bot) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + bot.getDifyApiKey());

        request.setResponseMode("blocking");
        String json = JSONObject.toJSONString(request);

        log.info("dify 聊天参数: {}", json);
        log.info("dify 聊天header参数: {}", JSONObject.toJSONString(headers));

        String url = properties.getBaseurl() + CHAT_MESSAGE;
        log.info("dify 聊天url: {}", url);

        String responseStr = HttpClientUtil.postJson(url, headers, json);
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        if (jsonObject.containsKey("message")) {
            String message = jsonObject.getString("message");
            if (message.contains("Conversation Not Exists.")) {
                // 去掉会话ID重新请求一次
                request.setConversationId("");
                json = JSONObject.toJSONString(request);
                url = properties.getBaseurl() + CHAT_MESSAGE;
                responseStr = HttpClientUtil.postJson(url, headers, json);
                log.info("second try ai chat response {}", responseStr);
                return JSONObject.parseObject(responseStr, DifyApiChatResponse.class);
            }
        }
        return JSONObject.parseObject(responseStr, DifyApiChatResponse.class);
    }
}
