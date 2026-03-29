package com.infinite.prism.moss.model.impl;

import com.alibaba.fastjson2.JSONObject;
import com.infinite.prism.moss.model.DTO.ChatCompletionResponse;
import com.infinite.prism.moss.model.DTO.CommonRequest;
import com.infinite.prism.moss.model.ImageToTextProvider;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.Set;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/9 21:17
 */
@Slf4j
@Component
public class WanXiangImageToTextProvider implements ImageToTextProvider {

    private final WebClient webClient;

    public WanXiangImageToTextProvider(WebClient.Builder webClientBuilder,
                                    @Value("${dashscope.api-key}") String apiKey) {
        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE);
        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                //.baseUrl("https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    @Override
    public boolean supports(String model) {
        return Set.of("qwen3.5-plus").contains(model);
    }

    @Override
    public String imageToText(CommonRequest request) {
        log.info("WanX API request body:  {}", JSONObject.toJSONString(request));
        ChatCompletionResponse response = webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .block();

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty() || response.getChoices().get(0) == null || response.getChoices().get(0).getMessage() == null) {
            throw new RuntimeException("WanX API returned empty response");
        }
        return response.getChoices().get(0).getMessage().getContent();
    }
}
