package com.infinite.prism.moss.controller;

import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.entity.request.LoginWithCodeRequest;
import com.infinite.prism.moss.entity.request.SendCodeRequest;
import com.infinite.prism.moss.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public ApiResultResponse<?> sendCode(@Validated @RequestBody SendCodeRequest request) {
        authService.sendCode(request);
        return ApiResultResponse.ok("验证码已发送至邮箱");
    }

    /**
     * 验证码登录/注册
     */
    @PostMapping("/login-with-code")
    public ApiResultResponse<?> loginWithCode(@Validated @RequestBody LoginWithCodeRequest request) {
        return authService.loginWithCode(request);
    }
}
