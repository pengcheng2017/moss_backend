package com.infinite.prism.moss.model.impl;

import com.alibaba.fastjson2.JSONObject;
import com.infinite.prism.moss.model.DTO.ImageToImageRequest;
import com.infinite.prism.moss.model.DTO.WanXTextToImageRequest;
import com.infinite.prism.moss.model.DTO.WanXTextToImageResponse;
import com.infinite.prism.moss.model.ImageToImageProvider;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/5 17:44
 */
@Slf4j
@Component
public class WanXImageToImageProvider implements ImageToImageProvider {

    private final WebClient webClient;

    public WanXImageToImageProvider(WebClient.Builder webClientBuilder,
                                   @Value("${dashscope.api-key}") String apiKey) {
        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE);
        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                //.baseUrl("https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation")
                .baseUrl("https://dashscope-intl.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }
    @Override
    public String generateImage(ImageToImageRequest req) {
        try {
            if (CollectionUtils.isEmpty(req.getFatherImageUrls())) {
                throw new RuntimeException("not upload father image");
            }
            if (CollectionUtils.isEmpty(req.getMatherImageUrls())) {
                throw new RuntimeException("not upload mather image");
            }
            WanXTextToImageRequest.Content promptContent = new WanXTextToImageRequest.Content(req.getPrompt(), null);
            WanXTextToImageRequest.Content fatherImageContent = new WanXTextToImageRequest.Content(null, req.getFatherImageUrls().get(0));
            WanXTextToImageRequest.Content matherImageContent = new WanXTextToImageRequest.Content(null, req.getMatherImageUrls().get(0));
            WanXTextToImageRequest.Message message = new WanXTextToImageRequest.Message("user", List.of(promptContent, fatherImageContent, matherImageContent));
            WanXTextToImageRequest.Input input = new WanXTextToImageRequest.Input(List.of(message));
            WanXTextToImageRequest.Parameters parameters = new WanXTextToImageRequest.Parameters(
                    "", true, false, req.getSize()
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

    @Override
    public boolean supports(String model) {
        return Set.of("wan2.6-image").contains(model);
    }
}
