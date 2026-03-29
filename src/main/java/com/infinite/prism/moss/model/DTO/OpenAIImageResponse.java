package com.infinite.prism.moss.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OpenAIImageResponse {

    private List<ImageData> data;

    @Data
    @AllArgsConstructor
    public static class ImageData {
        private String url;
    }

    public static OpenAIImageResponse of(String url) {
        return new OpenAIImageResponse(
                List.of(new ImageData(url))
        );
    }
}
