package com.infinite.prism.moss.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DashScopeClient {

    private final WebClient webClient;

    public DashScopeClient(
            @Value("${dashscope.api-key}") String apiKey
    ) {
        this.webClient = WebClient.builder()
                .baseUrl("https://dashscope.aliyuncs.com/api/v1/services/aigc")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public <T> T post(String path, Object body, Class<T> respType) {
        return webClient.post()
                .uri(path)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(respType)
                .block();
    }
}
