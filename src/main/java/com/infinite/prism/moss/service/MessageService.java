package com.infinite.prism.moss.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.infinite.prism.moss.utils.RegexUtil.extractImageUrl;

@Slf4j
@Service
public class MessageService {

    private final WhatsAppService whatsAppService;


    public MessageService(WhatsAppService whatsAppService, ThreadPoolTaskExecutor taskExecutor) {
        this.whatsAppService = whatsAppService;

    }

    public void sendMessage(String message, String contactsId) {
        try {
            log.info("message is {}", message);
            List<String> imageUrls = extractImageUrl(message);
            if (!imageUrls.isEmpty()) {
                String imageUrl = imageUrls.get(0);
                String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.indexOf("?"));
                whatsAppService.sendImageMessage(imageUrl, imageName, message.replace(imageUrl, ""), contactsId);
            } else {
                whatsAppService.sendMessage(message, contactsId);
            }
        } catch (Exception e) {
            log.error("send message failed", e);
        }
    }
}