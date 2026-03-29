package com.infinite.prism.moss.service.factory;

import com.infinite.prism.moss.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * 测评服务工厂，根据类型英文名获取对应的服务实现
 */
@Component
public class QuizServiceFactory {

    private final Map<String, QuizService> quizServiceMap;

    @Autowired
    public QuizServiceFactory(Map<String, QuizService> quizServiceMap) {
        this.quizServiceMap = quizServiceMap;
    }

    /**
     * 根据测评类型英文名获取对应的服务实现
     * @param type 测评类型英文名，如 "matchQuiz", "soulPortraitQuiz"
     * @return QuizService 实现
     * @throws IllegalArgumentException 当类型不支持时抛出
     */
    public QuizService getQuizService(String type) {
        QuizService service = quizServiceMap.get(type);
        if (service == null) {
            throw new IllegalArgumentException("Unsupported quiz type: " + type);
        }
        return service;
    }

    /**
     * 可选：获取所有支持的类型列表
     */
    public Map<String, QuizService> getAllServices() {
        return quizServiceMap;
    }
}