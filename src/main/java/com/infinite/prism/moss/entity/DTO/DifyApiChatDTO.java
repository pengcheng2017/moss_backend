package com.infinite.prism.moss.entity.DTO;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.HashMap;

/**
 *
 *
 * @author liao.peng
 * @since 2025/11/2 17:49
 */
@Data
public class DifyApiChatDTO {

    @JSONField(name = "inputs")
    private HashMap<String, Object> inputs = new HashMap<>();

    @JSONField(name = "query")
    private String query;

    @JSONField(name = "response_mode")
    private String responseMode;

    @JSONField(name = "conversation_id")
    private String conversationId = "";

    @JSONField(name = "user")
    private String user = "";

    /**
     *  dify flow id
     */
    @JSONField(name = "flow_id")
    private String flowId;

    @JSONField(name = "api_key")
    private String apiKey;
}
