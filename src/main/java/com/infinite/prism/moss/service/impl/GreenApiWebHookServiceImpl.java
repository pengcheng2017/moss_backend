package com.infinite.prism.moss.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.infinite.prism.moss.entity.DO.Customer;
import com.infinite.prism.moss.entity.DTO.DifyApiChatDTO;
import com.infinite.prism.moss.entity.DTO.DifyApiChatResponse;
import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.entity.VO.UserVO;
import com.infinite.prism.moss.service.ChatToAiService;
import com.infinite.prism.moss.service.CustomerService;
import com.infinite.prism.moss.service.MessageService;
import com.infinite.prism.moss.service.WebhookService;
import com.infinite.prism.moss.utils.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/11 11:08
 */
@Slf4j
@Service
public class GreenApiWebHookServiceImpl implements WebhookService {

    @Resource
    private CustomerService customerService;

    @Resource
    private ChatToAiService chatToAiService;

    @Resource
    private MessageService messageService;

    public void AIReply(JSONObject textMessageWebhook) {
        String incomingMessage = null;
        if (!textMessageWebhook.containsKey("messageData")) {
            log.error("messageData is null");
            return;
        }
        JSONObject messageData = textMessageWebhook.getJSONObject("messageData");
        if (messageData.containsKey("textMessageData")){
            incomingMessage = messageData.getJSONObject("textMessageData").getString("textMessage");
        } else if (messageData.containsKey("extendedTextMessageData")){
            incomingMessage = messageData.getJSONObject("extendedTextMessageData").getString("text");
        }
        String typeMessage = messageData.getString("typeMessage");
        String imageUrl = null;
        if ("imageMessage".equals(typeMessage)) {
            JSONObject fileMessageData = messageData.getJSONObject("fileMessageData");
            imageUrl = fileMessageData.getString("downloadUrl");
        }

        if (incomingMessage == null && imageUrl == null) {
            log.error("incomingMessage is null");
            log.info("webhook message is {}", textMessageWebhook.toJSONString());
            return;
        }
        String senderId = textMessageWebhook.getJSONObject("instanceData").getString("wid");
        // 查询是否开启ai自动回复
        UserVO userVO = UserContext.getUserVO();
        if (userVO == null) {
            log.error("userVO is null");
            return;
        }
        String chatId = textMessageWebhook.getJSONObject("senderData").getString("chatId");
        Customer customer = customerService.getCustomerWithTags(chatId);
        if (customer == null) {
            customer = new Customer();
            customer.setId(chatId);
            customer.setNickname(chatId);
            customer.setUserId(userVO.getId());
            customer.setEnableAiReply(true);
            customerService.saveCustomer(customer);
        }
        if (!customer.getEnableAiReply()) {
            // 如果未开启ai自动回复, 直接返回
            return;
        }
        // 尝试查询会话id
        String conversationId = customerService.getCustomerConversationId(textMessageWebhook.getJSONObject("senderData").getString("chatId"),
                userVO.getId());
        // 调用AI接口
        DifyApiChatDTO difyApiChatDTO = new DifyApiChatDTO();
        if (imageUrl != null && incomingMessage != null) {
            difyApiChatDTO.setQuery(incomingMessage + " imageUrl: " + imageUrl);
        } else if (incomingMessage != null){
            difyApiChatDTO.setQuery(incomingMessage);
        } else {
            difyApiChatDTO.setQuery("imageUrl: " + imageUrl);
        }
        // 若有会话id则使用, 没有则使用空串, 表示开启首次AI会话
        difyApiChatDTO.setConversationId(StringUtils.hasLength(conversationId) ? conversationId : "");
        difyApiChatDTO.setUser(senderId);
        ApiResultResponse<DifyApiChatResponse> aiResponse = chatToAiService.chat(difyApiChatDTO);
        if (200 == aiResponse.getCode()) {
            // 需要在dify端统一返回格式
            String aiResponseMessage = aiResponse.getData().getAnswer();
            if (aiResponseMessage == null) {
                log.error("ai reply failed");
                return;
            }
            // 记录会话ID
            customerService.updateConversationId(textMessageWebhook.getJSONObject("senderData").getString("chatId"),
                    userVO.getId(),
                    aiResponse.getData().getConversationId());
            // 使用AI回复内容去回复对方
            messageService.sendMessage(aiResponseMessage, textMessageWebhook.getJSONObject("senderData").getString("chatId"));
        }
    }

    @Override
    public List<String> getLastConversations(String contactId) {
        return List.of();
    }
}
