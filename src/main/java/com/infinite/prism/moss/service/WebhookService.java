package com.infinite.prism.moss.service;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

public interface WebhookService {

    public void AIReply(JSONObject textMessageWebhook);

    /**
     * 获取最近的聊天记录
     *
     * @param contactId 联系人Id
     * @return 最近的聊天记录
     */
    List<String> getLastConversations(String contactId);

}
