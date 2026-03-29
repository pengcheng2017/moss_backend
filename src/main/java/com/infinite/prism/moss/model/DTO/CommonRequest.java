package com.infinite.prism.moss.model.DTO;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 顶层请求对象，对应JSON中的根结构
 */
@Data
public class CommonRequest {
    private String model;
    private List<Message> messages;

    /**
     * 消息对象，对应messages数组中的元素
     */
    @Data
    public static class Message {
        private String role;
        private List<ContentItem> content;
    }

    /**
     * 内容项，对应content数组中的元素，支持两种类型：image_url 和 text
     */
    @Data
    public static class ContentItem {
        private String type;

        @JsonProperty("image_url")
        private ImageUrl imageUrl;

        private String text;
    }

    /**
     * 图片URL对象，对应image_url字段的值
     */
    @Data
    public static class ImageUrl {
        private String url;
    }
}





