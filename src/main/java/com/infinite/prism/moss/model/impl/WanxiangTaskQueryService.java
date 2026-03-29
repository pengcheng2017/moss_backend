package com.infinite.prism.moss.model.impl;

import com.infinite.prism.moss.model.DTO.VideoResult;
import com.infinite.prism.moss.model.DTO.VideoStatus;
import com.infinite.prism.moss.model.DTO.WanxiangTaskResult;
import com.infinite.prism.moss.model.DashScopeClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WanxiangTaskQueryService {

    private final DashScopeClient client;

    public WanxiangTaskQueryService(DashScopeClient client) {
        this.client = client;
    }

    public VideoResult query(String taskId) {

        WanxiangTaskResult resp =
                client.post("/task/query",
                        Map.of("task_id", taskId),
                        WanxiangTaskResult.class);

        if ("SUCCEEDED".equals(resp.getOutput().getTaskStatus())) {
            return new VideoResult(
                    taskId,
                    VideoStatus.SUCCESS,
                    resp.getOutput().getResults().get(0).getUrl()
            );
        }

        return new VideoResult(taskId, VideoStatus.PENDING);
    }
}
