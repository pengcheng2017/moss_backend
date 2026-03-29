package com.infinite.prism.moss.constants.enums;

public enum ErrorCode {
    SUCCESS(200, "Operation successful"),
    RATE_LIMIT_EXCEEDED(429, "The request is too frequent. Please try again later"),
    BATCH_MESSAGE_LIMIT(6001, "The number of batch message sends today has reached the upper limit (2 times)"),
    VERIFICATION_CODE_EXPIRED(4001, "The verification code has expired"),
    VERIFICATION_CODE_INVALID(4002, "Incorrect verification code"),
    VERIFICATION_CODE_ERROR(4004, "验证码错误"),
    VERIFICATION_CODE_LOCKED(4005, "验证码错误次数过多，请15分钟后重试"),
    EMAIL_SEND_FAILED(5001, "Email sending failed"),
    EMAIL_FORMAT_ERROR(4003, "Email format error"),
    EMAIL_SEND_TOO_FREQUENT(4006, "验证码发送过于频繁，请60秒后重试"),
    EMAIL_DOMAIN_NOT_ALLOWED(4007, "邮箱域名不允许注册"),
    USER_CREATE_FAILED(5002, "用户创建失败");


    private final int code;
    private final String message;

    public String getMessage() {
        return message;
    }

    public int getCode(){
        return code;
    }

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}