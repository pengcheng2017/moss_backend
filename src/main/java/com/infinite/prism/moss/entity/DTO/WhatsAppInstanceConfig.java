package com.infinite.prism.moss.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppInstanceConfig {

    private String wid;

    private String countryInstance;

    private String typeAccount;

    private String webhookUrl;

    private String webhookUrlToken;

    private Integer delaySendMessagesMilliseconds;

    private String markIncomingMessagesReaded;

    private String markIncomingMessagesReadedOnReply;

    private String sharedSession;

    private String proxyInstance;

    private String outgoingWebhook;

    private String outgoingMessageWebhook;

    private String outgoingAPIMessageWebhook;

    private String incomingWebhook;

    private String deviceWebhook;

    private String statusInstanceWebhook;

    private String stateWebhook;

    private String enableMessagesHistory;

    private String keepOnlineStatus;

    private String pollMessageWebhook;

    private String incomingBlockWebhook;

    private String incomingCallWebhook;

    private String editedMessageWebhook;

    private String deletedMessageWebhook;
}