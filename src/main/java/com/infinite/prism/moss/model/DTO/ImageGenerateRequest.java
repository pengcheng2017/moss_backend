package com.infinite.prism.moss.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageGenerateRequest {

    /** 模型名称，例如 wanx-v1 */
    private String model;

    /** 文本提示 */
    private String prompt;

    /** 图片大小，例如 "1024x1024" 或 "512x512" */
    private String size;

    /** 生成数量，默认 1 */
    private Integer n;

    /** 可选：随机种子 */
    private Long seed;

    /** 可选：风格、质量等扩展参数 */
    private String style;

    /** 工厂方法：从 OpenAIImageRequest 转换 */
    public static ImageGenerateRequest from(OpenAIImageRequest req) {
        return ImageGenerateRequest.builder()
                .model(req.getModel())
                .prompt(req.getPrompt())
                .size(req.getSize())   // 默认大小
                .n(1)                // 默认数量
                .build();
    }
}
