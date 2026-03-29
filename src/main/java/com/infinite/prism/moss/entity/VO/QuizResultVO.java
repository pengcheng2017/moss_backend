package com.infinite.prism.moss.entity.VO;

import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/6 15:06
 */
@Data
public class QuizResultVO {

    public List<QuizResultItem> results;

    @Data
    public static class QuizResultItem {
        private String type;

        private String suggest;
    }
}
