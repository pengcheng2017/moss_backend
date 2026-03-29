package com.infinite.prism.moss.model.DTO;

import lombok.Data;
import java.util.List;

/**
 * 顶层响应对象，对应 JSON 根结构
 */
@Data
public class ChatCompletionResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private Object system_fingerprint;  // 可能为 null 或字符串，使用 Object 兼容
    private List<Choice> choices;
    private Usage usage;

    /**
     * 选项对象，对应 choices 数组中的元素
     */
    @Data
    public static class Choice {
        private Message message;
        private String finish_reason;
        private int index;
        private Object logprobs;  // 可能为 null 或对象，使用 Object 兼容
    }

    /**
     * 消息对象，对应 message 字段
     */
    @Data
    public static class Message {
        private String role;
        private String content;
    }

    /**
     * 使用量对象，对应 usage 字段
     */
    @Data
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}