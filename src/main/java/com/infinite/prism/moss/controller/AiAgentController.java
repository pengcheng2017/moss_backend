package com.infinite.prism.moss.controller;

import com.alibaba.fastjson2.JSONObject;
import com.infinite.prism.moss.entity.DTO.DifyApiChatDTO;
import com.infinite.prism.moss.entity.DTO.DifyApiChatResponse;
import com.infinite.prism.moss.entity.DTO.PersonProfile;
import com.infinite.prism.moss.entity.VO.ApiResultResponse;
import com.infinite.prism.moss.entity.request.InfoGenRequest;
import com.infinite.prism.moss.service.ChatToAiService;
import com.infinite.prism.moss.utils.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.infinite.prism.moss.utils.JsonConverter.convertToJsonString;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/10 17:14
 */
@Slf4j
@RestController
@RequestMapping("/aiAgent")
public class AiAgentController {

    @Resource
    private ChatToAiService chatToAiService;


    @PostMapping("/infoGen")
    public ApiResultResponse<PersonProfile> infoGen(@RequestBody InfoGenRequest request) {
        try {
            UserContext.setUserId(2L);
            DifyApiChatDTO chatDTO = new DifyApiChatDTO();
            if (StringUtils.hasLength(request.getQuestion())) {
                chatDTO.setQuery(request.getQuestion());
            } else {
                chatDTO.setQuery("姓名:" + request.getName() + " 性别: " + request.getGender() + "出生年月日时分秒: " + request.getBirthDatetime());
            }
            chatDTO.getInputs().put("name", request.getName());
            chatDTO.getInputs().put("gender", request.getGender());
            chatDTO.getInputs().put("birthDatetime", request.getBirthDatetime());

            chatDTO.setUser("user");
            chatDTO.setResponseMode("block");
            ApiResultResponse<DifyApiChatResponse> chat = chatToAiService.chat(chatDTO);
            String answer = convertToJsonString(chat.getData().getAnswer());
            try {
                PersonProfile personProfile = JSONObject.parseObject(answer, PersonProfile.class);
                return ApiResultResponse.ok(personProfile);
            } catch (Exception e) {
                PersonProfile personProfile = new PersonProfile();
                personProfile.setAnswer(answer);
                return ApiResultResponse.ok(personProfile);
            }
        } catch (Exception e) {
            log.error("生成人物信息出错", e);
            return ApiResultResponse.error("gen person info faield");
        }
    }

}
