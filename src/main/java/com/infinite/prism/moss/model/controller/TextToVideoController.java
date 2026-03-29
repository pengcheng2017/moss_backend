package com.infinite.prism.moss.model.controller;

import com.infinite.prism.moss.model.DTO.TextToVideoRequest;
import com.infinite.prism.moss.model.DTO.VideoResult;
import com.infinite.prism.moss.model.ModelRouter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/videos")
public class TextToVideoController {

    private final ModelRouter router;

    public TextToVideoController(ModelRouter router) {
        this.router = router;
    }

    @PostMapping("/text")
    public VideoResult generate(@RequestBody TextToVideoRequest req) {
        return router.textToVideo(req);
    }
}
