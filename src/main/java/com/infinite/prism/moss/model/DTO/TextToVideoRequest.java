package com.infinite.prism.moss.model.DTO;

import lombok.Data;

@Data
public class TextToVideoRequest {
    private String model;
    private String prompt;
    private Integer duration;
}
