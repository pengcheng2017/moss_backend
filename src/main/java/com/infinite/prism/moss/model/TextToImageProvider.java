package com.infinite.prism.moss.model;


import com.infinite.prism.moss.constants.generate.GenerateType;
import com.infinite.prism.moss.model.DTO.ImageGenerateRequest;

public interface TextToImageProvider extends ModelProvider {

    String generateImage(ImageGenerateRequest request);

    @Override
    default GenerateType type() {
        return GenerateType.TEXT_TO_IMAGE;
    }
}
