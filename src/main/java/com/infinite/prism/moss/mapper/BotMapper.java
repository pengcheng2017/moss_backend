package com.infinite.prism.moss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.infinite.prism.moss.entity.DO.Bot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BotMapper extends BaseMapper<Bot> {

    @Select("SELECT * FROM bots WHERE user_id = #{userId} limit 1")
    Bot selectByUserId(@Param("userId") Long userId);

}
