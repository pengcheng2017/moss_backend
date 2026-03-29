package com.infinite.prism.moss.model.DTO;

import lombok.Data;

@Data
public class ImageToVideoRequest {
    private String model;
    private String imageUrl;
    private String prompt;
    private Integer duration;
}
