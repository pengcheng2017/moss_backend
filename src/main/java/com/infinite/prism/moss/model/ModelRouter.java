package com.infinite.prism.moss.model;


import com.infinite.prism.moss.constants.generate.GenerateType;
import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.model.DTO.*;
import com.infinite.prism.moss.service.impl.MinioStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;



@Slf4j
@Component
public class ModelRouter {

    private final List<ModelProvider> providers;
    private final MinioStorageService minioStorageService;

    public ModelRouter(List<ModelProvider> providers,
                       MinioStorageService minioStorageService) {
        this.providers = providers;
        this.minioStorageService = minioStorageService;
    }

    public String textToImage(ImageGenerateRequest request) {
        return providers.stream()
                .filter(p -> p.type() == GenerateType.TEXT_TO_IMAGE)
                .map(p -> (TextToImageProvider) p)
                .filter(p -> p.supports(request.getModel()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Model does not support TEXT_TO_IMAGE: " + request.getModel()))
                .generateImage(request);
    }

    public VideoResult textToVideo(TextToVideoRequest request) {
        return providers.stream()
                .filter(p -> p.type() == GenerateType.TEXT_TO_VIDEO)
                .map(p -> (TextToVideoProvider) p)
                .filter(p -> p.supports(request.getModel()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Model does not support TEXT_TO_VIDEO: " + request.getModel()))
                .generateVideo(request);
    }

    public VideoResult imageToVideo(ImageToVideoRequest request) {
        return providers.stream()
                .filter(p -> p.type() == GenerateType.IMAGE_TO_VIDEO)
                .map(p -> (ImageToVideoProvider) p)
                .filter(p -> p.supports(request.getModel()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Model does not support IMAGE_TO_VIDEO: " + request.getModel()))
                .generateVideo(request);
    }

    public ApiResultResponse<String> imageToImage(ImageToImageRequest request) {
        try {
            String url = providers.stream()
                    .filter(p -> p.type() == GenerateType.IMAGE_TO_IMAGE)
                    .map(p -> (ImageToImageProvider) p)
                    .filter(p -> p.supports(request.getModel()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Model does not support IMAGE_TO_IMAGE: " + request.getModel()))
                    .generateImage(request);
            return ApiResultResponse.ok(minioStorageService.uploadFromUrlAndGetPresignedUrl(url, "jodohku", UUID.randomUUID().toString().replace("-", "")));
        } catch (Exception e) {
            log.error("image to image failed", e);
            return ApiResultResponse.error("image to image failed");
        }
    }

    public ApiResultResponse<String> imageToText(CommonRequest request) {
        try {
            // 查找支持该模型的 ImageToTextProvider（失败直接抛出异常，不重试）
            ImageToTextProvider provider = providers.stream()
                    .filter(p -> p.type() == GenerateType.IMAGE_TO_TEXT)
                    .map(p -> (ImageToTextProvider) p)
                    .filter(p -> p.supports(request.getModel()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Model does not support IMAGE_TO_IMAGE: " + request.getModel()));

            int maxAttempts = 3;
            Exception lastException = null;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    String text = provider.imageToText(request);
                    return ApiResultResponse.ok(text);
                } catch (Exception e) {
                    lastException = e;
                    if (attempt == maxAttempts) {
                        // 最后一次尝试仍失败，抛出异常供外层 catch 处理
                        throw e;
                    } else {
                        log.warn("imageToText attempt {} failed, retrying...", attempt, e);
                        // 可根据需要添加短暂休眠，避免过快重试
                        // try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    }
                }
            }
            // 理论上不会执行到这里
            throw new IllegalStateException("Unexpected flow");
        } catch (Exception e) {
            log.error("image to text failed", e);
            return ApiResultResponse.error("image to text failed");
        }
    }
}
