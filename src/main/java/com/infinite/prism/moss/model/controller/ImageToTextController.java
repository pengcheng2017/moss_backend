package com.infinite.prism.moss.model.controller;

import com.alibaba.fastjson2.JSONObject;
import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.entity.request.ImageContentRequest;
import com.infinite.prism.moss.model.DTO.CommonRequest;
import com.infinite.prism.moss.model.ModelRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/9 21:24
 */
@Slf4j
@RestController
@RequestMapping("/v1/images")
public class ImageToTextController {

    private final ModelRouter router;

    public ImageToTextController(ModelRouter router) {
        this.router = router;
    }

    @PostMapping("/checkPayed")
    public ApiResultResponse<String> imageToText(@RequestBody ImageContentRequest request) {
        try {
            if (!StringUtils.hasLength(request.getImageUrl())) {
                return ApiResultResponse.ok("未付款");
            }
            String json = "{\n" +
                    "  \"model\": \"qwen3.5-plus\",\n" +
                    "  \"messages\": [\n" +
                    "  {\n" +
                    "    \"role\": \"user\",\n" +
                    "    \"content\": [\n" +
                    "      {\"type\": \"image_url\", \"image_url\": {\"url\": \"" + request.getImageUrl() + "\"}},\n" +
                    "      {\"type\": \"text\", \"text\": \"图中描绘的是什么?\"}\n" +
                    "    ]\n" +
                    "  }]\n" +
                    "}";
            CommonRequest req = JSONObject.parseObject(json, CommonRequest.class);
            return router.imageToText(req);
        } catch (Exception e) {
            log.error("image to text failed", e);
            return ApiResultResponse.error("image to text failed");
        }
    }

}
