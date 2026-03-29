package com.infinite.prism.moss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.infinite.prism.moss.entity.DO.UserDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    UserDO selectByEmail(String email);

}
