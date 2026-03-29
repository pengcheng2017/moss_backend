package com.infinite.prism.moss.controller;

import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.utils.IndonesianNameGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 网名控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/username")
public class UsernameController {

    @Autowired
    private IndonesianNameGenerator nameGenerator;

    /**
     * 批量生成候选网名
     */
    @GetMapping("/generate")
    public ApiResultResponse<List<String>> generateUsernames(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) String gender) {
        if (count < 1 || count > 50) {
            count = 10;
        }
        List<String> usernames = nameGenerator.generateNames(count, gender);
        return ApiResultResponse.ok("生成成功", usernames);
    }

    /**
     * 验证网名是否可用
     */
    @GetMapping("/validate")
    public ApiResultResponse<Boolean> validateUsername(@RequestParam String username) {
        boolean isValid = nameGenerator.isNameAvailable(username);
        String message = isValid ? "网名可用" : "网名不可用";
        return ApiResultResponse.ok(message, isValid);
    }

    /**
     * 重新生成单个网名
     */
    @GetMapping("/regenerate")
    public ApiResultResponse<String> regenerateUsername(
            @RequestParam(required = false) String gender) {
        String username = nameGenerator.generateSingleName(gender);
        return ApiResultResponse.ok("生成成功", username);
    }
}
