package com.infinite.prism.moss.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class EnvironmentVariableValidator implements CommandLineRunner {

    @Value("${dashscope.api-key:}")
    private String dashscopeApiKey;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${minio.secret-key:}")
    private String minioSecretKey;

    @Override
    public void run(String... args) {
        List<String> missingVariables = new ArrayList<>();

        if (dashscopeApiKey == null || dashscopeApiKey.trim().isEmpty() ||
            dashscopeApiKey.startsWith("${") || dashscopeApiKey.endsWith("}")) {
            missingVariables.add("DASHSCOPE_API_KEY");
        }

        if (mailPassword == null || mailPassword.trim().isEmpty() ||
            mailPassword.startsWith("${") || mailPassword.endsWith("}")) {
            missingVariables.add("MAIL_PASSWORD");
        }

        if (minioSecretKey == null || minioSecretKey.trim().isEmpty() ||
            minioSecretKey.startsWith("${") || minioSecretKey.endsWith("}")) {
            missingVariables.add("MINIO_SECRET_KEY");
        }

        if (!missingVariables.isEmpty()) {
            String errorMessage = "应用启动失败：以下必需的环境变量未设置或格式不正确: " +
                    String.join(", ", missingVariables) + "\n" +
                    "请在系统环境变量或启动参数中设置这些变量，例如：\n" +
                    "export DASHSCOPE_API_KEY=your_api_key\n" +
                    "export MAIL_PASSWORD=your_mail_password\n" +
                    "export MINIO_SECRET_KEY=your_minio_secret_key";
            log.error(errorMessage);
            throw new IllegalStateException("缺少必需的环境变量: " + String.join(", ", missingVariables));
        }

        log.info("环境变量验证通过，所有必需的配置已正确加载");
    }
}
