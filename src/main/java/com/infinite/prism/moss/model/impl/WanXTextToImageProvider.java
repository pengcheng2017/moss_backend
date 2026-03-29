package com.infinite.prism.moss.model.impl;

import com.alibaba.fastjson2.JSONObject;
import com.infinite.prism.moss.constants.generate.GenerateType;
import com.infinite.prism.moss.model.DTO.ImageGenerateRequest;
import com.infinite.prism.moss.model.DTO.WanXTextToImageRequest;
import com.infinite.prism.moss.model.DTO.WanXTextToImageResponse;
import com.infinite.prism.moss.model.TextToImageProvider;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;


import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class WanXTextToImageProvider implements TextToImageProvider {

    private final WebClient webClient;

    public WanXTextToImageProvider(WebClient.Builder webClientBuilder,
                                   @Value("${dashscope.api-key}") String apiKey) {
        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE);
        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    @Override
    public GenerateType type() {
        return GenerateType.TEXT_TO_IMAGE;
    }

    @Override
    public boolean supports(String model) {
        // 可支持多个模型
        return Set.of("qwen-image-plus", "wanx-v1", "wanx-v2", "qwen-image-2.0-pro").contains(model);
    }

    @Override
    public String generateImage(ImageGenerateRequest req) {
        try {
            WanXTextToImageRequest.Content content = new WanXTextToImageRequest.Content(req.getPrompt(), null);
            WanXTextToImageRequest.Message message = new WanXTextToImageRequest.Message("user", List.of(content));
            WanXTextToImageRequest.Input input = new WanXTextToImageRequest.Input(List.of(message));
            WanXTextToImageRequest.Parameters parameters = new WanXTextToImageRequest.Parameters(
                    "text, watermark, signature, blurry, low quality, lowres, bad anatomy, extra limbs, extra fingers, deformed, holding things in hand, modern frame, plain background, nsfw, worst quality, normal quality, monochrome, 3d render, photo, photograph", true, false, req.getSize()
            );

            WanXTextToImageRequest requestBody = new WanXTextToImageRequest();
            requestBody.setModel(req.getModel());
            requestBody.setInput(input);
            requestBody.setParameters(parameters);
            log.info("WanX API request body:  {}", JSONObject.toJSONString(requestBody));
            WanXTextToImageResponse response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(WanXTextToImageResponse.class)
                    .block();

            if (response == null || response.getOutput() == null || response.getOutput().getChoices().isEmpty()) {
                throw new RuntimeException("WanX API returned empty response");
            }

            WanXTextToImageResponse.Choice choice = response.getOutput().getChoices().get(0);
            if (choice.getMessage() == null || choice.getMessage().getContent().isEmpty()) {
                throw new RuntimeException("WanX API choice content empty");
            }

            String imageUrl = choice.getMessage().getContent().get(0).getImage();
            if (imageUrl == null) {
                throw new RuntimeException("WanX API did not return image URL");
            }

            return imageUrl;
        } catch (Exception e) {
            log.error("WanX API call failed", e);
            throw new RuntimeException(e);
        }
    }
}
