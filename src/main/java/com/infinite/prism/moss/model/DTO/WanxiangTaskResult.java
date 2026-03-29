package com.infinite.prism.moss.model.DTO;

import lombok.Data;

import java.util.List;

@Data
public class WanxiangTaskResult {

    private String requestId;
    private Output output;

    @Data
    public static class Output {

        /** 任务 ID */
        private String taskId;

        /** 任务状态：PENDING / RUNNING / SUCCEEDED / FAILED */
        private String taskStatus;

        /** 成功结果（视频 / 图片） */
        private List<Result> results;

        /** 失败原因 */
        private String errorMessage;

        public boolean isSucceeded() {
            return "SUCCEEDED".equalsIgnoreCase(taskStatus);
        }

        public boolean isFailed() {
            return "FAILED".equalsIgnoreCase(taskStatus);
        }
    }

    @Data
    public static class Result {

        /** 视频或图片 URL */
        private String url;
    }
}
