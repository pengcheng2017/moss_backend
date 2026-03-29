package com.infinite.prism.moss.entity.DTO;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 *
 * @author liao.peng
 * @since 2025/11/2 17:57
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class DifyApiChatResponse extends BaseApiChatResponse{

    private String event;

    @JSONField(name = "task_id")
    private String taskId;

    private String id;

    @JSONField(name = "message_id")
    private String messageId;

    @JSONField(name = "conversation_id")
    private String conversationId;

    @JSONField(name = "created_at")
    private Long createdAt;

    private Metadata metadata;

    @Data
    public static class Metadata {
        @JSONField(name = "annotation_reply")
        private Object annotationReply;

        @JSONField(name = "retriever_resources")
        private Object[] retrieverResources;

        private Usage usage;

        @Data
        public static class Usage {
            @JSONField(name = "prompt_tokens")
            private Integer promptTokens;

            @JSONField(name = "prompt_unit_price")
            private String promptUnitPrice;

            @JSONField(name = "prompt_price_unit")
            private String promptPriceUnit;

            @JSONField(name = "prompt_price")
            private String promptPrice;

            @JSONField(name = "completion_tokens")
            private Integer completionTokens;

            @JSONField(name = "completion_unit_price")
            private String completionUnitPrice;

            @JSONField(name = "completion_price_unit")
            private String completionPriceUnit;

            @JSONField(name = "completion_price")
            private String completionPrice;

            @JSONField(name = "total_tokens")
            private Integer totalTokens;

            @JSONField(name = "total_price")
            private String totalPrice;

            private String currency;
            private Double latency;
        }
    }
}
