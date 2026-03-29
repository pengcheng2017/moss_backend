package com.infinite.prism.moss.entity.DTO;

import lombok.Data;

/**
 * person profile
 */
@Data
public class PersonProfile {
    private String answer;
    private Attribute personality;
    private Attribute wealth;
    private Attribute weakness;
    private Attribute meeting_scene;
    private Attribute conflict;
    private Attribute cheating_risk;

    /**
     * 表示每个特性（如personality、wealth等）的内部类
     */
    @Data
    public static class Attribute {
        private String content;
        private String reason;
    }
}