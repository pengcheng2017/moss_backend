package com.infinite.prism.moss.entity.DTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 *  顾客信息
 *
 * @author liao.peng
 * @since 2025/11/7 16:32
 */
@Data
public class CustomerResponse {

    /**
     * 顾客账号
     */
    private String id;

    /**
     * 账号
     */
    private String account;

    private Long userId;

    /**
     * 昵称
     */
    private String name;

    /**
     * 标签
     */
    private List<Long> tags;

    private String tagsStr;

    private String status =  "active";

    /**
     * 推送时间
     */
    private String pushTime;

    /**
     * 推送开关
     */
    private Boolean pushEnabled;

    /**
     * 推送模板
     */
    private String messageTemplate;

    /**
     * 最后推送时间
     */
    private LocalDateTime lastPushTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
