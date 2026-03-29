package com.infinite.prism.moss.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.infinite.prism.moss.entity.DO.Bot;
import com.infinite.prism.moss.entity.VO.ApiResultResponse;

public interface BotService extends IService<Bot> {
    Bot getBotByUserId(Long userId);

    Bot getBotByUserId();

    ApiResultResponse<Page<Bot>> pageList(Integer pageNum, Integer pageSize);

    Bot getOneBot(String name);

    boolean updateBot(Bot bot);
}
