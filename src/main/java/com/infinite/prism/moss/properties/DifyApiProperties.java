package com.infinite.prism.moss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *  与dify api 相关的配置
 *
 * @author liao.peng
 * @since 2025/11/2 16:43
 */
@Data
@ConfigurationProperties(prefix = "com.sales.helper.dify.chat.api")
@Component
public class DifyApiProperties {

    private String baseurl;

    private String consoleBaseurl;

    private String openEndpoint;

    private String collectApiKey;

    private String scheduleActivityKey;

    private String email;

    private String password;

    private String language;

    private Boolean rememberMe = true;

    private String uploadPath = "/tmp";

}
