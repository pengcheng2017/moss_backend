package com.infinite.prism.moss.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WanXTextToImageRequest {

    private String model;

    private Input input;

    private Parameters parameters;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Input {
        private List<Message> messages;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role; // "user"
        private List<Content> content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        private String text;

        private String image;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Parameters {
        private String negative_prompt;
        private Boolean prompt_extend;
        private Boolean watermark;
        private String size;
    }
}
