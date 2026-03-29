package com.infinite.prism.moss.model.DTO;

import lombok.Data;

import java.util.List;

@Data
public class WanXTextToImageResponse {

    private Output output;

    @Data
    public static class Output {
        private List<Choice> choices;
        private TaskMetric task_metric;
    }

    @Data
    public static class Choice {
        private String finish_reason;
        private Message message;
    }

    @Data
    public static class Message {
        private String role;
        private List<Content> content;
    }

    @Data
    public static class Content {
        private String image;  // 返回图片 URL
    }

    @Data
    public static class TaskMetric {
        private Integer TOTAL;
        private Integer FAILED;
        private Integer SUCCEEDED;
    }
}
