package com.infinite.prism.moss.service.impl;


import com.infinite.prism.moss.constants.enums.ErrorCode;
import com.infinite.prism.moss.exception.BusinessException;
import com.infinite.prism.moss.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    private final String fromEmail;
    
    // 验证码邮件模板
    private static final String VERIFICATION_EMAIL_TEMPLATE = "<!DOCTYPE html>\n" +
            "<html lang=\"zh-CN\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>验证码通知</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: Arial, sans-serif;\n" +
            "            line-height: 1.6;\n" +
            "            color: #333;\n" +
            "            max-width: 600px;\n" +
            "            margin: 0 auto;\n" +
            "            padding: 20px;\n" +
            "        }\n" +
            "        .container {\n" +
            "            background-color: #f9f9f9;\n" +
            "            padding: 30px;\n" +
            "            border-radius: 8px;\n" +
            "            box-shadow: 0 2px 4px rgba(0,0,0,0.1);\n" +
            "        }\n" +
            "        .code {\n" +
            "            font-size: 24px;\n" +
            "            font-weight: bold;\n" +
            "            color: #4CAF50;\n" +
            "            margin: 20px 0;\n" +
            "            padding: 10px;\n" +
            "            background-color: #f0f0f0;\n" +
            "            display: inline-block;\n" +
            "            border-radius: 4px;\n" +
            "        }\n" +
            "        .footer {\n" +
            "            margin-top: 30px;\n" +
            "            font-size: 12px;\n" +
            "            color: #666;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <h2>验证码通知</h2>\n" +
            "        <p>亲爱的 %s，</p>\n" +
            "        <p>您的验证码是：<span class=\"code\">%s</span></p>\n" +
            "        <p>验证码有效期为 %s 分钟，请及时使用。</p>\n" +
            "        <p>如果您没有请求此验证码，请忽略此邮件。</p>\n" +
            "        <div class=\"footer\">\n" +
            "            <p>此邮件由系统自动发送，请勿回复。</p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
    
    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, 
                           @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }
    
    @Override
    @Async("emailTaskExecutor")
    public void sendVerificationCode(String toEmail, String username, String code) {
        try {
            String content = String.format(VERIFICATION_EMAIL_TEMPLATE, "user", code, "5");
            sendMimeEmail(toEmail, "验证码通知", content);
            log.info("验证码邮件发送成功: {}", toEmail);
        } catch (Exception e) {
            log.error("发送验证码邮件失败: {}", toEmail, e);
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED,
                    "邮件发送失败: " + e.getMessage());
        }
    }
    
    @Override
    public void sendSimpleEmail(String toEmail, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("发送简单邮件失败: {}", toEmail, e);
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
    
    private void sendMimeEmail(String toEmail, String subject, String htmlContent)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
}