package com.infinite.prism.moss.model;


import com.infinite.prism.moss.constants.generate.GenerateType;
import com.infinite.prism.moss.model.DTO.TextToVideoRequest;
import com.infinite.prism.moss.model.DTO.VideoResult;

public interface TextToVideoProvider extends ModelProvider {

    VideoResult generateVideo(TextToVideoRequest request);

    @Override
    default GenerateType type() {
        return GenerateType.TEXT_TO_VIDEO;
    }
}
