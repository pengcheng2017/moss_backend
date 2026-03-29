package com.infinite.prism.moss.entity.DO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户Green API配置实体类
 * 对应表：user_green_api
 */
@Data
@TableName("user_green_api")
public class UserGreenApi  {

    @TableId
    @TableField("instance_id")
    private String instanceId;

    @TableField(value = "username", exist = false)
    private String username;

    @TableField(value = "balance", exist = false)
    private BigDecimal balance;

    @TableField(value = "phone", exist = false)
    private String phone;

    @TableField("user_id")
    private Long userId;
    
    @TableField("token")
    private String token;
    
    @TableField("base_url")
    private String baseUrl;
    
    @TableField("incoming_url")
    private String incomingUrl;
    
    @TableField("outgoing_url")
    private String outgoingUrl;
    
    @TableField("send_url")
    private String sendUrl;

    @TableField("qr_code_url")
    private String qrCodeUrl;

    @TableField("email")
    private String email;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField("follow_up_status")
    private String followUpStatus;

    @TableField("use_webhook")
    private Integer useWebhook = 1;
    
    // 注意：user_id和self_whatsapp_account字段继承自父类
}