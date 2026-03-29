package com.infinite.prism.moss.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinite.prism.moss.entity.DO.UserDO;
import com.infinite.prism.moss.entity.DO.UserGreenApi;
import com.infinite.prism.moss.entity.VO.UserVO;
import com.infinite.prism.moss.mapper.UserGreenApiMapper;
import com.infinite.prism.moss.mapper.UserMapper;
import com.infinite.prism.moss.utils.HttpClientUtil;
import com.infinite.prism.moss.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WhatsAppService {

    private final UserGreenApiMapper userGreenApiMapper;

    private final UserMapper userMapper;

    public WhatsAppService(ObjectMapper objectMapper,
                           UserGreenApiMapper userGreenApiMapper,
                           UserMapper userMapper) {
        this.userGreenApiMapper = userGreenApiMapper;
        this.userMapper = userMapper;
    }



    private UserGreenApi getUserGreenApi() {
        UserVO userVO = UserContext.getUserVO();
        if (userVO == null) {
            throw new RuntimeException("please login first");
        }
        if (StringUtils.isEmpty(userVO.getMainAccountId())) {
            // 首次登录并没有将mainAccountId设置到token中
            QueryWrapper<UserDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", userVO.getId());
            UserDO userDO = userMapper.selectOne(queryWrapper);
            userVO.setMainAccountId(userDO.getMainAccountId());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userVO.getId());
        List<UserGreenApi> list = userGreenApiMapper.selectByMap(map);
        if (list.isEmpty()) {
            throw new RuntimeException("please bind whatsapp account first for userId " + userVO.getId());
        }
        return list.get(0);
    }

    public String sendMessage(String message, String contactsId) {
        try {
            HashMap<String, Object> postBody = new HashMap<>();
            postBody.put("message", message);
            postBody.put("chatId", contactsId);
            UserGreenApi userGreenApi = getUserGreenApi();
            String s = HttpClientUtil.postJson(userGreenApi.getSendUrl(), JSONObject.toJSONString(postBody));
            log.info("Send message response: {}", s);
            JSONObject jsonObject = JSONObject.parseObject(s);
            return jsonObject.getString("idMessage");
        } catch (Exception e) {
            log.error("Failed to send message", e);
            throw new RuntimeException(e);
        }
    }

    public String sendImageMessage(String urlFile, String fileName, String caption, String contactsId) {
        try {
            HashMap<String, Object> postBody = new HashMap<>();
            postBody.put("fileName", fileName);
            postBody.put("urlFile", urlFile);
            postBody.put("caption", caption);
            postBody.put("chatId", contactsId);
            UserGreenApi userGreenApi = getUserGreenApi();
            String url = userGreenApi.getBaseUrl() + "/sendFileByUrl/" + userGreenApi.getToken();
            String requestBody = JSONObject.toJSONString(postBody);
            log.info("send image url {} and request body {}", url, requestBody);
            String s = HttpClientUtil.postJson(url, requestBody);
            log.info("Send image message response: {}", s);
            JSONObject jsonObject = JSONObject.parseObject(s);
            return jsonObject.getString("idMessage");
        } catch (Exception e) {
            log.error("Failed to send message", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取whatsapp 关联 验证码
     *
     * @param phoneNumber 手机号码
     * @return 关联验证码
     */
    public String getAuthorizationCode(Long phoneNumber) {
        try {
            UserGreenApi userGreenApi = getUserGreenApi();
            String url = userGreenApi.getBaseUrl() + "/getAuthorizationCode/" + userGreenApi.getToken();
            HashMap<String, Object> params = new HashMap<>();
            params.put("phoneNumber", phoneNumber);
            String s = HttpClientUtil.postJson(url, JSONObject.toJSONString(params));

            JSONObject jsonObject = JSONObject.parseObject(s);
            if (jsonObject.getBoolean("status")) {
                return jsonObject.getString("code");
            }
            throw new RuntimeException("failed to get whatsapp link authorization code");
        } catch (Exception e) {
            log.error("failed to get whatsapp link authorization code");
            throw new RuntimeException("failed to get whatsapp link authorization code", e);
        }
    }
}