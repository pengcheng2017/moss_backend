package com.infinite.prism.moss.entity.DO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("customer")
public class Customer {

    /**
     * 主账号下的联系人 id
     */
    private String id;

    /**
     * 主账号 id
     */
    @TableField("main_account_id")
    private String mainAccountId;

    @TableField("user_id")
    private Long userId;
    
    @TableField("nickname")
    private String nickname;

    /**
     * 格式: "HH:mm" 如 "09:30", "14:45"
     */
    @TableField("push_time")
    private String pushTime;

    /**
     * 客户所在时区
     */
    @TableField("time_zone")
    private String timeZone = "Asia/Jakarta";

    @TableField("push_enabled")
    private Boolean pushEnabled = false;

    @TableField("enable_ai_reply")
    private Boolean enableAiReply = false;

    @TableField("last_push_time")
    private LocalDateTime lastPushTime;

    @TableField("message_template")
    private String messageTemplate;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("customer_avatar")
    private String customerAvatar;

}