package com.infinite.prism.moss.model.controller;

import com.infinite.prism.moss.model.DTO.ImageToVideoRequest;
import com.infinite.prism.moss.model.DTO.VideoResult;
import com.infinite.prism.moss.model.ModelRouter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/videos")
public class ImageToVideoController {

    private final ModelRouter router;

    public ImageToVideoController(ModelRouter router) {
        this.router = router;
    }

    @PostMapping("/image")
    public VideoResult generate(@RequestBody ImageToVideoRequest req) {
        return router.imageToVideo(req);
    }
}
