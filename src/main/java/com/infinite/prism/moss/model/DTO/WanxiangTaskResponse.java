package com.infinite.prism.moss.model.DTO;

import lombok.Data;

@Data
public class WanxiangTaskResponse {

    private Output output;

    @Data
    public static class Output {
        private String taskId;
    }
}
