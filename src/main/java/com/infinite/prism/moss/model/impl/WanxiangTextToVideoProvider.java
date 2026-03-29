package com.infinite.prism.moss.model.impl;

import com.infinite.prism.moss.constants.generate.GenerateType;
import com.infinite.prism.moss.model.DTO.TextToVideoRequest;
import com.infinite.prism.moss.model.DTO.VideoResult;
import com.infinite.prism.moss.model.DTO.VideoStatus;
import com.infinite.prism.moss.model.DTO.WanxiangTaskResponse;
import com.infinite.prism.moss.model.DashScopeClient;
import com.infinite.prism.moss.model.TextToVideoProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WanxiangTextToVideoProvider
        implements TextToVideoProvider {

    private final DashScopeClient client;

    public WanxiangTextToVideoProvider(DashScopeClient client) {
        this.client = client;
    }

    @Override
    public GenerateType type() {
        return GenerateType.TEXT_TO_VIDEO;
    }

    @Override
    public boolean supports(String model) {
        return "wanx-v2".equals(model);
    }

    @Override
    public VideoResult generateVideo(TextToVideoRequest req) {

        Map<String, Object> body = Map.of(
                "model", req.getModel(),
                "input", Map.of(
                        "prompt", req.getPrompt()
                ),
                "parameters", Map.of(
                        "duration", 5
                )
        );

        WanxiangTaskResponse resp =
                client.post("/text2video", body, WanxiangTaskResponse.class);

        return new VideoResult(
                resp.getOutput().getTaskId(),
                VideoStatus.PENDING
        );
    }
}
