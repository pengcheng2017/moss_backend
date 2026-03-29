package com.infinite.prism.moss.model;


import com.infinite.prism.moss.constants.generate.GenerateType;
import com.infinite.prism.moss.model.DTO.ImageToVideoRequest;
import com.infinite.prism.moss.model.DTO.VideoResult;

public interface ImageToVideoProvider extends ModelProvider {

    VideoResult generateVideo(ImageToVideoRequest request);

    @Override
    default GenerateType type() {
        return GenerateType.IMAGE_TO_VIDEO;
    }
}
