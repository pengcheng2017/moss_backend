package com.infinite.prism.moss.entity.DTO;

import lombok.Data;

@Data
public class PersonInfo  {
    // 肖像图片地址
    private String portrait;
    // 性格
    private String personality;
    // 财富状况
    private String wealth;
    // 弱点
    private String weakness;
    // 相遇场景
    private String meetingScene;
    // 冲突
    private String conflict;
    // 出轨风险
    private String cheatingRisk;
}