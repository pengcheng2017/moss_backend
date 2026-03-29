package com.infinite.prism.moss.model.controller;

import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.model.DTO.ImageToImageRequest;
import com.infinite.prism.moss.model.ModelRouter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/5 17:08
 */
@RestController
@RequestMapping("/v1/images")
public class ImageToImageController {

    private final ModelRouter router;

    public ImageToImageController(ModelRouter router) {
        this.router = router;
    }

    @PostMapping("/imageToImage")
    public ApiResultResponse<String> generate(@RequestBody ImageToImageRequest req) {
        return router.imageToImage(req);
    }
}
