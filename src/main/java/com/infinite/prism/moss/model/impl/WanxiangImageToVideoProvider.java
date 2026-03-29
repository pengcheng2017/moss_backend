package com.infinite.prism.moss.model.impl;

import com.infinite.prism.moss.constants.generate.GenerateType;
import com.infinite.prism.moss.model.DTO.ImageToVideoRequest;
import com.infinite.prism.moss.model.DTO.VideoResult;
import com.infinite.prism.moss.model.DTO.VideoStatus;
import com.infinite.prism.moss.model.DTO.WanxiangTaskResponse;
import com.infinite.prism.moss.model.DashScopeClient;
import com.infinite.prism.moss.model.ImageToVideoProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WanxiangImageToVideoProvider
        implements ImageToVideoProvider {

    private final DashScopeClient client;

    public WanxiangImageToVideoProvider(DashScopeClient client) {
        this.client = client;
    }

    @Override
    public GenerateType type() {
        return GenerateType.IMAGE_TO_VIDEO;
    }

    @Override
    public boolean supports(String model) {
        return "wanx-i2v".equals(model);
    }

    @Override
    public VideoResult generateVideo(ImageToVideoRequest req) {

        Map<String, Object> body = Map.of(
                "model", req.getModel(),
                "input", Map.of(
                        "image_url", req.getImageUrl(),
                        "prompt", req.getPrompt()
                )
        );

        WanxiangTaskResponse resp =
                client.post("/image2video", body, WanxiangTaskResponse.class);

        return new VideoResult(resp.getOutput().getTaskId(), VideoStatus.PENDING);
    }
}
