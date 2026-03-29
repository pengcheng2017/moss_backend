package com.infinite.prism.moss.entity.DO;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bots")
public class Bot {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机器人名称
     */
    @TableField("name")
    private String name;

    /**
     * 机器人描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否公开(所有用户共享)
     */
    @TableField("is_public")
    private Boolean isPublic = false;

    /**
     * 用户ID, -1表示共享
     */
    @TableField("user_id")
    private Long userId;

    /**
     * dify api key
     */
    @TableField("dify_api_key")
    private String difyApiKey;

    @TableField("to_live_agent_condition")
    private String toLiveAgentCondition;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
