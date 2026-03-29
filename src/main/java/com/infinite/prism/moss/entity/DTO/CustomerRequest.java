package com.infinite.prism.moss.entity.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 *
 *
 * @author liao.peng
 * @since 2025/11/7 16:36
 */
@Data
public class CustomerRequest {

    private String nickname;

    @NotNull
    private String id;

    /**
     * 主账号ID
     */
    private String mainAccountId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     *  标签ID 多值
     */
    private String tagIds;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 推送消息模版
     */
    private String messageTemplate;

    /**
     * 客户所在时区
     */
    private String timeZone;

    /**
     * 推送时间
     */
    private String pushTime;

    /**
     * 是否推送
     */
    private Boolean pushEnabled;

    /**
     * 是否AI回复
     */
    private Boolean enableAiReply = null;

    /**
     * 头像
     */
    private String customerAvatar;
}
