package com.infinite.prism.moss.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author liao.peng
 * @since 2025/10/7 22:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private Long id;

    /**
     * 绑定的whatsapp 账号
     */
    private String mainAccountId;

    private String username;

    /**
     * 真实姓名
     */
    private String truename;

    private String token;

    private List<String> roles;

    private final Set<String> permissions = new HashSet<>();

    private String introduction;

    private String name;

    private String avatar;

    private String email;

    private String phone;

    private String industry;

    /**
     * 转化状态
     */
    private Integer conversionStatus;

    /**
     * 转化负责人
     */
    private String personInCharge;

    private String remark;

    private String createTime;

    private String updateTime;

    /**
     * 账户积分余额
     */
    private java.math.BigDecimal balance;

    private Boolean enableAutoReply;

    private String appAddress;
}