package com.infinite.prism.moss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.infinite.prism.moss.entity.DO.Customer;
import com.infinite.prism.moss.entity.DTO.CustomerRequest;
import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.entity.VO.UserVO;
import com.infinite.prism.moss.mapper.CustomerMapper;
import com.infinite.prism.moss.mapper.UserMapper;
import com.infinite.prism.moss.service.CustomerService;
import com.infinite.prism.moss.utils.UserContext;
import io.jsonwebtoken.lang.Strings;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public Customer getCustomerWithTags(String customerId) {
        UserVO userVO = UserContext.getUserVO();
        if (userVO == null) {
            throw new RuntimeException("please login first");
        }
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        wrapper.eq("id", customerId)
                .eq("user_id", userVO.getId());
        return getOne(wrapper);
    }

    @Override
    public void clearAvatarCache() {
        customerMapper.clearCustomerAvatar();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveCustomer(Customer customer) {

        UserVO userVO = UserContext.getUserVO();
        if (userVO == null) {
            throw new RuntimeException("please login first");
        }
        customer.setUserId(userVO.getId());
        // 先查询是否存在
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<Customer>().eq("user_id", userVO.getId()).eq("id", customer.getId()).last("limit 1");
        Customer old = getOne(queryWrapper);
        boolean saved;
        if (old != null) {
            // 更新
            customerMapper.update(customer, queryWrapper);
            saved = true;
        } else {
            saved = save(customer);
        }
        return saved;
    }

    @Override
    public Optional<Customer> selectOneCustomer(String customerId) {
        try {
            UserVO userVO = UserContext.getUserVO();
            if (userVO == null) {
                throw new RuntimeException("please login first");
            }
            QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", customerId)
                    .eq("user_id", userVO.getId())
                    .last("limit 1");
            Customer customer = customerMapper.selectOne(queryWrapper);
            if (customer == null) {
                return Optional.empty();
            }
            return Optional.of(customer);
        } catch (Exception e) {
            log.error("查询失败", e);
            throw new RuntimeException("查询失败: ", e);
        }
    }

    /**
     * 获取会话ID
     *
     * @param customerId 客户 ID
     * @return 会话ID
     */
    @Override
    public String getCustomerConversationId(String customerId) {
        try {
            UserVO userVO = UserContext.getUserVO();
            if (userVO == null) {
                throw new RuntimeException("用户未登录");
            }
            return customerMapper.selectConversationId(customerId, userVO.getId());
        } catch (Exception e) {
            log.error("获取会话ID失败", e);
            throw new RuntimeException("获取会话ID失败: " + e.getMessage());
        }
    }

    @Override
    public String getCustomerConversationId(String customerId, Long userId) {
        try {

            return customerMapper.selectConversationIdByAccountId(customerId, userId);

        } catch (Exception e) {
            log.error("获取会话ID失败", e);
            throw new RuntimeException("获取会话ID失败: " + e.getMessage());
        }
    }

    /**
     * 更新会话ID
     *
     * @param customerId     顾客 id
     * @param conversationId 会话 ID
     * @return 更新结果
     */
    @Override
    public Boolean updateConversationId(String customerId, String conversationId) {
        try {
            UserVO userVO = UserContext.getUserVO();
            if (userVO == null) {
                throw new RuntimeException("用户未登录");
            }
            String s = customerMapper.selectConversationId(customerId, userVO.getId());
            if (s != null) {
                return customerMapper.updateConversationId(customerId, conversationId, userVO.getId());
            }

            return true;
        } catch (Exception e) {
            log.error("更新会话ID失败", e);
            throw new RuntimeException("更新会话ID失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResultResponse<Boolean> clearConversationMemory(Long userId) {
        try {
            customerMapper.clearConversationMemory(userId);
            return ApiResultResponse.ok("ok", Boolean.TRUE);
        } catch (Exception e) {
            log.error("clear conversation memory failed", e);
            return ApiResultResponse.error("clear conversation memory failed");
        }
    }

    @Override
    public Boolean updateConversationId(String customerId, Long userId, String conversationId) {
        try {
            String s = customerMapper.selectConversationIdByAccountId(customerId, userId);
            if (s == null) {
                return customerMapper.updateConversationIdByAccountId(customerId, conversationId, userId);
            }
            return true;
        } catch (Exception e) {
            log.error("更新会话ID失败", e);
            throw new RuntimeException("更新会话ID失败: " + e.getMessage());
        }
    }

    /**
     * 更新顾客信息
     *
     * @param customer 顾客信息
     * @return 是否更新成功
     */
    @Override
    public boolean updateCustomer(CustomerRequest customer) {
        if (customer == null) {
            return false;
        }
        UserVO userVO = UserContext.getUserVO();
        if (userVO == null) {
            throw new RuntimeException("please login first");
        }
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        wrapper.eq("id", customer.getId())
                .eq("user_id", userVO.getId());
        Customer customerEntity = customerMapper.selectOne(wrapper);
        if (customerEntity == null) {
            // 新增联系人
            customerEntity = new Customer();
            customerEntity.setId(customer.getId());
            customerEntity.setUserId(userVO.getId());
            customerEntity.setNickname(customer.getNickname() == null ? customer.getId() : customer.getNickname());
            if (customer.getPushEnabled() != null) {
                customerEntity.setPushEnabled(customer.getPushEnabled());
            }
            if (Strings.hasLength(customer.getPushTime())) {
                customerEntity.setPushTime(customer.getPushTime());
            }

            if (Strings.hasLength(customer.getMessageTemplate())) {
                customerEntity.setMessageTemplate(customer.getMessageTemplate());
            }

            if (customer.getEnableAiReply() != null) {
                customerEntity.setEnableAiReply(customer.getEnableAiReply());
            }
            return customerMapper.insert(customerEntity) > 0;
        }
        if (customer.getPushEnabled() != null) {
            customerEntity.setPushEnabled(customer.getPushEnabled());
        }
        if (Strings.hasLength(customer.getPushTime())) {
            customerEntity.setPushTime(customer.getPushTime());
        }

        if (Strings.hasLength(customer.getMessageTemplate())) {
            customerEntity.setMessageTemplate(customer.getMessageTemplate());
        }

        if (customer.getEnableAiReply() != null) {
            customerEntity.setEnableAiReply(customer.getEnableAiReply());
        }
        // 修改选中的标签
        return customerMapper.update(customerEntity, wrapper) > 0;
    }
}