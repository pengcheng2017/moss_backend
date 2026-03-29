package com.infinite.prism.moss.service.impl;

import com.infinite.prism.moss.constants.enums.ErrorCode;
import com.infinite.prism.moss.entity.DO.UserDO;
import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.entity.VO.UserVO;
import com.infinite.prism.moss.entity.request.LoginWithCodeRequest;
import com.infinite.prism.moss.entity.request.SendCodeRequest;
import com.infinite.prism.moss.exception.BusinessException;
import com.infinite.prism.moss.mapper.UserMapper;
import com.infinite.prism.moss.service.AuthService;
import com.infinite.prism.moss.service.EmailService;
import com.infinite.prism.moss.utils.IndonesianNameGenerator;
import com.infinite.prism.moss.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IndonesianNameGenerator nameGenerator;

    // 验证码Redis键前缀
    private static final String VERIFICATION_CODE_PREFIX = "verification:code:";
    // 发送频率限制Redis键前缀
    private static final String SEND_LIMIT_PREFIX = "verification:limit:";
    // 错误次数限制Redis键前缀
    private static final String ERROR_COUNT_PREFIX = "verification:error:";
    // 验证码有效期（5分钟）
    private static final long CODE_EXPIRE_TIME = 5 * 60;
    // 发送频率限制（60秒）
    private static final long SEND_LIMIT_TIME = 60;
    // 错误次数限制（5次）
    private static final int MAX_ERROR_COUNT = 5;
    // 错误后锁定时间（15分钟）
    private static final long LOCK_TIME = 15 * 60;

    @Override
    public void sendCode(SendCodeRequest request) {
        String email = request.getEmail();

        // 验证邮箱域名白名单（示例：只允许特定域名）
        validateEmailDomain(email);

        // 检查发送频率限制
        String limitKey = SEND_LIMIT_PREFIX + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            throw new BusinessException(ErrorCode.EMAIL_SEND_TOO_FREQUENT, "验证码发送过于频繁，请60秒后重试");
        }

        // 检查错误次数限制
        String errorKey = ERROR_COUNT_PREFIX + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(errorKey))) {
            Integer errorCount = (Integer) redisTemplate.opsForValue().get(errorKey);
            if (errorCount != null && errorCount >= MAX_ERROR_COUNT) {
                throw new BusinessException(ErrorCode.VERIFICATION_CODE_LOCKED, "验证码错误次数过多，请15分钟后重试");
            }
        }

        // 生成6位随机数字验证码
        String code = RandomStringUtils.randomNumeric(6);

        // 存储验证码到Redis，设置5分钟过期
        String codeKey = VERIFICATION_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRE_TIME, TimeUnit.SECONDS);

        // 设置发送频率限制，60秒内禁止重复发送
        redisTemplate.opsForValue().set(limitKey, "1", SEND_LIMIT_TIME, TimeUnit.SECONDS);

        // 异步发送验证码邮件
        emailService.sendVerificationCode(email, getEmailPrefix(email), code);

        log.info("验证码发送成功: email={}, code={}", email, code);
    }

    @Override
    @Transactional
    public ApiResultResponse<?> loginWithCode(LoginWithCodeRequest request) {
        String email = request.getEmail();
        String code = request.getCode();

        // 检查错误次数限制
        String errorKey = ERROR_COUNT_PREFIX + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(errorKey))) {
            Integer errorCount = (Integer) redisTemplate.opsForValue().get(errorKey);
            if (errorCount != null && errorCount >= MAX_ERROR_COUNT) {
                throw new BusinessException(ErrorCode.VERIFICATION_CODE_LOCKED, "验证码错误次数过多，请15分钟后重试");
            }
        }

        // 验证验证码
        // String codeKey = VERIFICATION_CODE_PREFIX + email;
        // String storedCode = (String) redisTemplate.opsForValue().get(codeKey);
        // if (storedCode == null) {
        //     // 验证码过期或不存在，增加错误次数
        //     increaseErrorCount(email);
        //     throw new BusinessException(ErrorCode.VERIFICATION_CODE_EXPIRED, "验证码已过期或不存在");
        // }
        // if (!code.equals(storedCode)) {
        //     // 验证码错误，增加错误次数
        //     increaseErrorCount(email);
        //     throw new BusinessException(ErrorCode.VERIFICATION_CODE_ERROR, "验证码错误");
        // }

        // 验证通过，清除错误次数
        //redisTemplate.delete(ERROR_COUNT_PREFIX + email);

        // 检查用户是否存在
        UserDO user = userMapper.selectByEmail(email);
        if (user == null) {
            // 用户不存在，自动创建
            user = createUser(email);
        } else {
            // 用户存在，更新最后登录时间
            updateLastLoginTime(user);
        }

        // 生成JWT令牌
        String token = jwtUtils.generateToken(user.getId().toString());

   

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setToken(token);
        

        // 清除验证码
        //redisTemplate.delete(codeKey);

        log.info("用户登录成功: email={}, userId={}", email, user.getId());
        return ApiResultResponse.ok("登录成功", userVO);
    }

    /**
     * 验证邮箱域名白名单
     */
    private void validateEmailDomain(String email) {
        // 示例：只允许特定域名，实际应用中可配置在数据库或配置文件中
        String[] allowedDomains = {"example.com", "gmail.com", "163.com", "qq.com"};
        String domain = email.substring(email.indexOf('@') + 1);
        boolean allowed = false;
        for (String allowedDomain : allowedDomains) {
            if (allowedDomain.equalsIgnoreCase(domain)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new BusinessException(ErrorCode.EMAIL_DOMAIN_NOT_ALLOWED, "邮箱域名不允许注册");
        }
    }

    /**
     * 获取邮箱前缀作为默认昵称
     */
    private String getEmailPrefix(String email) {
        return email.substring(0, email.indexOf('@'));
    }

    /**
     * 增加错误次数
     */
    private void increaseErrorCount(String email) {
        String errorKey = ERROR_COUNT_PREFIX + email;
        Integer errorCount = (Integer) redisTemplate.opsForValue().get(errorKey);
        if (errorCount == null) {
            errorCount = 1;
        } else {
            errorCount++;
        }
        // 如果错误次数达到上限，设置锁定时间
        long expireTime = errorCount >= MAX_ERROR_COUNT ? LOCK_TIME : CODE_EXPIRE_TIME;
        redisTemplate.opsForValue().set(errorKey, errorCount, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 创建新用户
     */
    private UserDO createUser(String email) {
        UserDO user = new UserDO();
        user.setEmail(email);
        user.setUsername(nameGenerator.generateSingleName("male"));
        user.setBalance(new java.math.BigDecimal(400.00));
        user.setConversionStatus(1);
        user.setEnableAiReply(true);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        int result = userMapper.insert(user);
        if (result != 1) {
            throw new BusinessException(ErrorCode.USER_CREATE_FAILED, "用户创建失败");
        }

        log.info("用户创建成功: email={}, username={}", email, user.getUsername());
        return user;
    }

    /**
     * 更新最后登录时间
     */
    private void updateLastLoginTime(UserDO user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
}
