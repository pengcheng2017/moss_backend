package com.infinite.prism.moss.model;


import com.infinite.prism.moss.constants.generate.GenerateType;
import com.infinite.prism.moss.model.DTO.ImageToImageRequest;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/5 17:34
 */

public interface ImageToImageProvider extends ModelProvider{
    @Override
    default GenerateType type() {
        return GenerateType.IMAGE_TO_IMAGE;
    }

    String generateImage(ImageToImageRequest request);

}
