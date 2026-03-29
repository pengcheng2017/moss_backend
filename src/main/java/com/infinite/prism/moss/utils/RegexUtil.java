package com.infinite.prism.moss.utils;

import lombok.extern.slf4j.Slf4j;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *
 * @author liao.peng
 * @since 2026/2/21 22:07
 */
@Slf4j
public class RegexUtil {

    public static List<String> extractImageUrls(String text) {
        List<String> urls = new ArrayList<>();
        // 正则：匹配http或https，然后域名（允许字母、数字、点、连字符），路径（允许字母、数字、下划线、斜杠、点、连字符），
        // 最后以常见的图片扩展名结尾，后面跟空白或字符串结束，以避免匹配多余字符。
        String regex = "https?://[\\w.-]+/[\\w./-]*\\.(jpg|jpeg|png|gif|bmp|svg|webp)(?=\\s|$|</)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            urls.add(matcher.group());
        }
        return urls;
    }

    /**
     *
     * @param text 包含图片地址的文本
     * @return 提取到的第一个图片 URL，如果没有找到则返回 null
     */
    public static List<String> extractImageUrl(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        // 将 \r\n 统一替换为 \n，避免重复替换
        text = text.replace("\r\n", "\n");
        // 将单独的 \r 替换为 \n
        text = text.replace("\r", "\n");
        // 将所有的 \n 替换为字面字符串 "\\n"
        text = text.replace("\n", "\\n");

        List<String> urls = new ArrayList<>();
        String regex = "\\s*(https?://\\S+[^\\n])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String s = matcher.group();
            if (s.contains("\\n")) {
                s = s.substring(0, s.indexOf("\\n"));
            }
            urls.add(s);
        }
        return urls;
    }


    /**
     * 从输入文本中提取第一行，判断是否为图片URL，如果是则返回该URL。
     *
     * @param text 输入文本，可能包含多行
     * @return 如果第一行是图片URL，则返回包含该URL的Optional；否则返回Optional.empty()
     */
    public static Optional<String> extractFirstLineIfImage(String text) {
        if (text == null || text.isEmpty()) {
            return Optional.empty();
        }
        log.info("extractFirstLineIfImage and text is {}", text);
        // 获取第一行（去除前后空白）
        Optional<String> firstLineOpt = text.lines()
                .findFirst()
                .map(String::trim);

        if (firstLineOpt.isEmpty() || firstLineOpt.get().isEmpty()) {
            return Optional.empty();
        }
        String firstLine = firstLineOpt.get();
        log.info("firstLine is {}", firstLine);
        return isImageUrl(firstLine) ? Optional.of(firstLine) : Optional.empty();
    }

    /**
     * 判断一个字符串是否为有效的图片URL（基于常见图片扩展名）。
     *
     * @param url 待检查的URL字符串
     * @return 如果是图片URL返回true，否则false
     */
    private static boolean isImageUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            if (path == null || path.isEmpty()) {
                return false;
            }

            // 提取文件扩展名（最后一个点之后的部分，忽略查询参数）
            int dotIndex = path.lastIndexOf('.');
            if (dotIndex == -1 || dotIndex == path.length() - 1) {
                return false;
            }

            String extension = path.substring(dotIndex + 1).toLowerCase();
            Set<String> imageExtensions = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp", "svg");
            return imageExtensions.contains(extension);

        } catch (URISyntaxException e) {
            // URL格式不合法，返回false
            return false;
        }
    }

}
