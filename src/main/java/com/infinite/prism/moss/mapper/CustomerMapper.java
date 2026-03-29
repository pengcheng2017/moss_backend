package com.infinite.prism.moss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.infinite.prism.moss.entity.DO.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    @Update("update customer set enable_ai_reply = #{isOpen} where user_id = #{userId}")
    void switchAutoReply(@Param("isOpen") Integer isOpen, @Param("userId") Long userId);

    @Update("UPDATE customer SET conversation_id = null WHERE user_id = #{userId}")
    void clearConversationMemory(@Param("userId") Long userId);

    @Update("update customer set customer_avatar = null")
    void clearCustomerAvatar();

    /**
     * 查询顾客会话id
     */
    @Select("SELECT conversation_id FROM customer WHERE id = #{customerId} and user_id = #{userId}")
    String selectConversationId(@Param("customerId") String customerId, @Param("userId") Long userId);

    @Select("SELECT conversation_id FROM customer WHERE id = #{customerId} and user_id = #{userId}")
    String selectConversationIdByAccountId(@Param("customerId") String customerId, @Param("userId") Long userId);

    @Select("UPDATE customer SET conversation_id = #{conversationId} WHERE id = #{customerId} and user_id = #{userId}")
    Boolean updateConversationIdByAccountId(@Param("customerId") String customerId, @Param("conversationId") String conversationId, @Param("userId") Long userId);

    /**
     * 保存顾客会话id
     */
    @Select("UPDATE customer SET conversation_id = #{conversationId} WHERE id = #{customerId} and user_id = #{userId}")
    Boolean updateConversationId(@Param("customerId") String customerId, @Param("conversationId") String conversationId,  @Param("userId") Long userId);

}