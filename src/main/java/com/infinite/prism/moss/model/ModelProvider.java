package com.infinite.prism.moss.model;


import com.infinite.prism.moss.constants.generate.GenerateType;

public interface ModelProvider {

    GenerateType type();

    boolean supports(String model);
}
