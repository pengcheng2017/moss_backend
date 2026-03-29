package com.infinite.prism.moss.model.DTO;

import lombok.Data;

import java.util.List;

@Data
public class WanxiangImageResponse {

    private Output output;

    @Data
    public static class Output {
        private List<Image> images;
    }

    @Data
    public static class Image {
        private String url;
    }
}
