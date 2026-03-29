package com.infinite.prism.moss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.infinite.prism.moss.entity.DO.Customer;
import com.infinite.prism.moss.entity.DTO.CustomerRequest;
import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import jakarta.validation.Valid;

import java.util.Optional;

public interface CustomerService extends IService<Customer> {

    Customer getCustomerWithTags(String customerId);

    void clearAvatarCache();
    
    boolean saveCustomer(Customer customer);

    /**
     *  联系人信息
     * @param customerId 联系人ID
     * @return 联系人信息
     */
    Optional<Customer> selectOneCustomer(String customerId);

    /**
     * 获取顾客会话ID
     * @param customerId 顾客 id
     * @return 会话 ID
     */
    String getCustomerConversationId(String customerId);

    String getCustomerConversationId(String customerId, Long userId);

    /**
     * 更新顾客会话ID
     * @param customerId 顾客 id
     * @param conversationId 会话 ID
     * @return 是否更新成功
     */
    Boolean updateConversationId(String customerId, String conversationId);

    ApiResultResponse<Boolean> clearConversationMemory(Long userId);

    Boolean updateConversationId(String customerId, Long userId, String conversationId);

    /**
     * 更新顾客信息
     * @param customer 顾客信息
     * @return 是否更新成功
     */
    boolean updateCustomer(@Valid CustomerRequest customer);
}