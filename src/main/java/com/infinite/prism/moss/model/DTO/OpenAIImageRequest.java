package com.infinite.prism.moss.model.DTO;

import lombok.Data;

@Data
public class OpenAIImageRequest {
    private String model = "qwen-image-2.0-pro";
    private String prompt;
    private String size = "1328*1328";
}
