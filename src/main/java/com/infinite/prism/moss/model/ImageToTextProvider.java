package com.infinite.prism.moss.model;

import com.infinite.prism.moss.constants.generate.GenerateType;
import com.infinite.prism.moss.model.DTO.CommonRequest;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/9 21:14
 */

public interface ImageToTextProvider extends ModelProvider{

    String imageToText(CommonRequest request);

    @Override
    default GenerateType type() {
        return GenerateType.IMAGE_TO_TEXT;
    }
}
