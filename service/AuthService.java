package com.infinite.prism.moss.service;

import com.infinite.prism.moss.entity.request.LoginWithCodeRequest;
import com.infinite.prism.moss.entity.request.SendCodeRequest;
import com.infinite.prism.moss.entity.VO.LoginResponse;

/**
 * 认证服务
 */
public interface AuthService {

    /**
     * 发送验证码
     * @param request 发送验证码请求
     */
    void sendCode(SendCodeRequest request);

    /**
     * 验证码登录/注册
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse loginWithCode(LoginWithCodeRequest request);
}
