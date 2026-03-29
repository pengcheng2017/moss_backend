package com.infinite.prism.moss.entity.DTO;

import lombok.Data;

import java.util.Map;

/**
 * 对应 JSON 根对象的实体类
 */
@Data
public class CollectInfoResult {
    /**
     * 决策类型，示例值为 "analyze"
     */
    private String decision;

    /**
     * 详细信息对象
     */
    private Map<String, String> info;
}