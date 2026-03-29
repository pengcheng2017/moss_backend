package com.infinite.prism.moss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.infinite.prism.moss.entity.DO.UserGreenApi;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserGreenApi Mapper接口
 */
@Mapper
public interface UserGreenApiMapper extends BaseMapper<UserGreenApi> {

    /**
     * 分页查询
     *
     * @param page 分页参数
     * @param keywords 关键字
     * @return IPage
     */
    IPage<UserGreenApi> pageList(Page<UserGreenApi> page, String keywords, String personInCharge);
}