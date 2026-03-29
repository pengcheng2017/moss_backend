package com.infinite.prism.moss.entity.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 *
 * @author liao.peng
 * @since 2025/10/7 21:27
 */
@Data
@TableName("user")
public class UserDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "main_account_id")
    private String mainAccountId;

    @TableField
    private String username;

    @TableField
    private String password;

    @TableField
    private String salt;

    @TableField
    private java.math.BigDecimal balance;

    @TableField
    private String email;

    @TableField
    private String phone;

    @TableField
    private String industry;

    @TableField
    private String truename;

    @TableField
    private String invitationCode;

    /**
     * 转化状态, 可用值见数据库字典表
     */
    @TableField
    private Integer conversionStatus;

    @TableField
    private String personInCharge;

    @TableField
    private String remark;

    @TableField
    private LocalDateTime createTime;

    @TableField
    private LocalDateTime updateTime;

    /**
     * 当日已发送批量消息次数
     */
    @TableField
    private Integer dailyBatchCount;

    /**
     * 最后发送批量消息日期（仅日期部分）
     */
    @TableField
    private LocalDate lastBatchDate;

    @TableField("enable_ai_reply")
    private Boolean enableAiReply = true;

    @TableField("app_address")
    private String appAddress;
}