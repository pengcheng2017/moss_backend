package com.infinite.prism.moss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.infinite.prism.moss.entity.DO.Bot;
import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.entity.VO.UserVO;
import com.infinite.prism.moss.mapper.BotMapper;
import com.infinite.prism.moss.mapper.UserMapper;
import com.infinite.prism.moss.properties.DifyApiProperties;
import com.infinite.prism.moss.service.BotService;
import com.infinite.prism.moss.utils.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Slf4j
@Service
public class BotServiceImpl extends ServiceImpl<BotMapper, Bot> implements BotService {

    @Resource
    private BotMapper botMapper;

    @Resource
    private UserMapper userMapper;


    @Resource
    private DifyApiProperties difyApiProperties;

    @Override
    public Bot getBotByUserId(Long userId) {
        UserVO userVO = UserContext.getUserVO();
        if (userVO == null) {
            throw new RuntimeException("please login again");
        }
        return botMapper.selectByUserId(userId);
    }

    @Override
    public Bot getBotByUserId() {
        UserVO userVO = UserContext.getUserVO();
        if (userVO == null) {
            throw new RuntimeException("please login again");
        }
        return botMapper.selectByUserId(userVO.getId());
    }

    @Override
    public ApiResultResponse<Page<Bot>> pageList(Integer pageNum, Integer pageSize) {
        try {
            UserVO userVO = UserContext.getUserVO();
            if (userVO == null) {
                throw new RuntimeException("please login first");
            }
            Page<Bot> page = new Page<>(pageNum, pageSize);
            QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userVO.getId());
            Page<Bot> botPage = botMapper.selectPage(page, queryWrapper);
            botPage.getRecords().forEach(bot -> {
                bot.setDifyApiKey("");
            });
            return ApiResultResponse.ok("ok", botPage);
        } catch (Exception e) {
            log.error("page list error", e);
            return ApiResultResponse.error("page list error: " + e.getMessage());
        }
    }

    @Override
    public Bot getOneBot(String name) {
        LambdaQueryWrapper<Bot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bot::getName, name);
        wrapper.last("limit 1");
        return botMapper.selectOne(wrapper);
    }

    @Override
    public boolean updateBot(Bot bot) {
        Bot byId = getById(bot.getId());
        if (byId == null) {
            throw new RuntimeException("bot not exist");
        }
        if (StringUtils.hasLength(bot.getToLiveAgentCondition())) {
            byId.setToLiveAgentCondition(bot.getToLiveAgentCondition());
        }
        return updateById(byId);
    }
}
