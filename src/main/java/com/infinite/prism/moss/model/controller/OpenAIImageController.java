package com.infinite.prism.moss.model.controller;

import com.infinite.prism.moss.model.DTO.ImageGenerateRequest;
import com.infinite.prism.moss.model.DTO.OpenAIImageRequest;
import com.infinite.prism.moss.model.DTO.OpenAIImageResponse;
import com.infinite.prism.moss.model.ModelRouter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/images")
@Tag(name = "Text To Image Operations", description = "Text To Image related operations")
public class OpenAIImageController {

    private final ModelRouter router;

    public OpenAIImageController(ModelRouter router) {
        this.router = router;
    }

    @PostMapping("/generations")
    @Operation(summary = "Text To Image", description = "Send a message to AI and get image response")
    public OpenAIImageResponse generate(@Parameter(name = "chat for image request parameters", required = true) @RequestBody OpenAIImageRequest req) {

        String url = router.textToImage(
                ImageGenerateRequest.from(req)
        );

        return OpenAIImageResponse.of(url);
    }

    @PostMapping("/textToImage")
    @Operation(summary = "Text To Image", description = "Send a message to AI and get image response")
    public String textToImage(@Parameter(name = "chat for image request parameters", required = true) @RequestBody OpenAIImageRequest req) {
        return router.textToImage(ImageGenerateRequest.from(req));
    }
}
