package com.infinite.prism.moss.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class HttpClientUtil {

    // 默认配置的OkHttpClient（推荐复用）
    private static OkHttpClient mClient;

    // 用于流式请求的OkHttpClient
    private static OkHttpClient streamClient;

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    private static class TrustAllCerts implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }


    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    // 初始化默认配置
    static {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 7897));
        mClient = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
                .hostnameVerifier(new TrustAllHostnameVerifier())
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(240, TimeUnit.SECONDS)
                //.proxy(proxy)
                .build();

        streamClient = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 设置自定义的OkHttpClient
     */
    public static void setClient(OkHttpClient client) {
        mClient = client;
    }

    public static Response newCall(Request request) {
        try {
            return mClient.newCall(request).execute();
        } catch (Exception e) {
            log.error("failed to request", e);
            throw new RuntimeException("failed to request", e);
        }
    }

    //======================== 流式请求处理 ========================

    /**
     * 发送流式请求并处理响应
     *
     * @param url 请求URL
     * @param method 请求方法 (GET/POST)
     * @param headers 请求头
     * @param requestBody 请求体 (POST请求使用)
     * @param streamHandler 流式响应处理器
     * @return 完整的响应结果
     * @throws IOException 请求异常
     */
    public static List<String> streamRequest(String url,
                                             String method,
                                             @Nullable Map<String, String> headers,
                                             @Nullable RequestBody requestBody,
                                             @Nullable StreamResponseHandler streamHandler) throws IOException {
        // 构建请求
        Request.Builder requestBuilder = new Request.Builder().url(url);

        // 添加请求头
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }

        // 设置请求方法和请求体
        if ("POST".equalsIgnoreCase(method)) {
            requestBuilder.post(requestBody != null ? requestBody : RequestBody.create("", null));
        } else {
            requestBuilder.get();
        }

        Request request = requestBuilder.build();
        //StringBuilder fullResponse = new StringBuilder();
        List<String> resultList = new ArrayList<>();

        // 发送请求并处理响应
        try (Response response = streamClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP错误码: " + response.code());
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("响应体为空");
            }

            try (BufferedSource source = body.source()) {
                while (!source.exhausted()) {
                    // 读取一行数据
                    String line = source.readUtf8Line();
                    if (line == null) {
                        break;
                    }

                    // 处理SSE格式的数据 (data: {...})
                    if (line.startsWith("data:")) {
                        String data = line.substring(5).trim();

                        // 跳过心跳消息
                        if (data.equals("[DONE]") || data.isEmpty()) {
                            continue;
                        }

                        // 解析JSON数据
                        try {
                            // 如果有自定义处理器，调用处理器处理数据
                            if (streamHandler != null) {
                                streamHandler.onMessage(data);
                            }

                            // 累加到完整响应
                            //fullResponse.append(data);
                            resultList.add(data);
                        } catch (Exception e) {
                            log.error("解析流式响应数据失败: {}", data, e);
                        }
                    }
                }
            }
        }

        return resultList;
    }

    /**
     * 发送流式GET请求
     *
     * @param url 请求URL
     * @param headers 请求头
     * @param params 请求参数
     * @param streamHandler 流式响应处理器
     * @return 完整的响应结果
     * @throws IOException 请求异常
     */
    public static List<String> streamGet(String url,
                                   @Nullable Map<String, String> headers,
                                   @Nullable Map<String, String> params,
                                   @Nullable StreamResponseHandler streamHandler) throws IOException {
        // 处理URL参数
        HttpUrl parse = HttpUrl.parse(url);
        if (parse == null) {
            throw new IllegalArgumentException("URL解析失败：" + url);
        }

        HttpUrl.Builder urlBuilder = parse.newBuilder();
        if (params != null) {
            params.forEach(urlBuilder::addQueryParameter);
        }

        return streamRequest(urlBuilder.build().toString(), "GET", headers, null, streamHandler);
    }

    /**
     * 发送流式POST请求 (JSON格式)
     *
     * @param url 请求URL
     * @param headers 请求头
     * @param jsonBody JSON请求体
     * @param streamHandler 流式响应处理器
     * @return 完整的响应结果
     * @throws IOException 请求异常
     */
    public static List<String> streamPostJson(String url,
                                        @Nullable Map<String, String> headers,
                                        String jsonBody,
                                        @Nullable StreamResponseHandler streamHandler) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, JSON);

        return streamRequest(url, "POST", headers, body, streamHandler);
    }

    /**
     * 流式响应处理器接口
     */
    public interface StreamResponseHandler {
        /**
         * 处理单条消息
         * @param message 消息内容
         */
        void onMessage(String message);

        /**
         * 处理完成回调
         * @param fullResponse 完整响应
         */
        void onComplete(String fullResponse);
    }

    /**
     * 简单的流式响应处理器实现，用于收集完整响应
     */
    public static class SimpleStreamHandler implements StreamResponseHandler {
        private final StringBuilder responseBuilder = new StringBuilder();
        private final Consumer<String> messageConsumer;
        private final Consumer<String> completeConsumer;

        public SimpleStreamHandler(Consumer<String> messageConsumer, Consumer<String> completeConsumer) {
            this.messageConsumer = messageConsumer;
            this.completeConsumer = completeConsumer;
        }

        @Override
        public void onMessage(String message) {
            if (messageConsumer != null) {
                messageConsumer.accept(message);
            }
            responseBuilder.append(message);
        }

        @Override
        public void onComplete(String fullResponse) {
            if (completeConsumer != null) {
                completeConsumer.accept(responseBuilder.toString());
            }
        }

        public String getFullResponse() {
            return responseBuilder.toString();
        }
    }

    //------------------------ 同步请求 ------------------------

    /**
     * 通用同步请求
     */
    public static Response execute(Request request) throws IOException {
        return mClient.newCall(request).execute();
    }

    /**
     * GET请求（同步）
     */
    public static String get(String url) throws IOException {
        return get(url, null, null);
    }

    public static String get(String url,
                             @Nullable Map<String, String> headers,
                             @Nullable Map<String, String> params) throws IOException {
        Request request = buildRequest(url, "GET", headers, params, null);
        return executeAndParseString(request);
    }

    /**
     * POST表单（同步）
     */
    public static String postForm(String url,
                                  Map<String, String> formParams) throws IOException {
        return postForm(url, null, formParams);
    }

    public static String postForm(String url,
                                  @Nullable Map<String, String> headers,
                                  Map<String, String> formParams) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        if (formParams != null) {
            formParams.forEach(builder::add);
        }
        Request request = buildRequest(url, "POST", headers, null, builder.build());
        return executeAndParseString(request);
    }

    /**
     * POST JSON（同步）
     */
    public static String postJson(String url, String json) throws IOException {
        return postJson(url, null, json);
    }

    public static String postJson(String url,
                                  @Nullable Map<String, String> headers,
                                  String json)  {
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(json, JSON);
            Request request = buildRequest(url, "POST", headers, null, body);
            return executeAndParseString(request);
        } catch (Exception e){
            log.error("请求异常", e);
            throw new RuntimeException(e);
        }
    }

    //------------------------ 异步请求 ------------------------

    /**
     * 通用异步请求
     */
    public static void enqueue(Request request, OnResultCallback callback) {
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (ResponseBody body = response.body()) {
                    String result = body != null ? body.string() : null;
                    callback.onSuccess(result);
                } catch (IOException e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    /**
     * 异步GET请求
     */
    public static void getAsync(String url,
                                OnResultCallback callback) {
        getAsync(url, null, null, callback);
    }

    public static void getAsync(String url,
                                @Nullable Map<String, String> headers,
                                @Nullable Map<String, String> params,
                                OnResultCallback callback) {
        Request request = buildRequest(url, "GET", headers, params, null);
        enqueue(request, callback);
    }



    // 其他异步方法类似，可根据需要添加...

    //------------------------ 工具方法 ------------------------

    private static String executeAndParseString(Request request) throws IOException {
        try (Response response = execute(request)) {
            // if (!response.isSuccessful()) throw new IOException("HTTP错误码: " + response.code());
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("响应体为空");
            }
            return body.string();
        } catch (Exception e) {
            log.error("请求异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建Request对象
     */
    private static Request buildRequest(String url,
                                        String method,
                                        @Nullable Map<String, String> headers,
                                        @Nullable Map<String, String> params,
                                        @Nullable RequestBody body) {
        HttpUrl parse = HttpUrl.parse(url);
        if (parse == null) {
            throw new IllegalArgumentException("URL解析失败：" + url);
        }
        // 处理URL参数
        HttpUrl.Builder urlBuilder = parse.newBuilder();
        if (params != null) {
            params.forEach(urlBuilder::addQueryParameter);
        }

        // 构建Request
        Request.Builder builder = new Request.Builder()
                .url(urlBuilder.build())
                .method(method, body);

        // 添加请求头
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        return builder.build();
    }


    /**
     * 回调接口
     */
    public interface OnResultCallback {
        void onSuccess(@Nullable String result);
        void onFailure(@NotNull Exception e);
    }

    //======================== 文件上传 ========================

    /**
     * 转换为File并指定目标目录
     */
    public static File convertToFile(MultipartFile multipartFile, String targetDir) throws IOException {
        // 确保目录存在
        File directory = new File(targetDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file;
        String originalFilename = multipartFile.getOriginalFilename() + "-" + System.currentTimeMillis();
        if(".\\tmp".equals(targetDir)) {
           file = new File(directory.getAbsolutePath().replace(".\\tmp", "\\tmp"), originalFilename);
        } else {
            file = new File(directory, originalFilename);
        }
        multipartFile.transferTo(file);
        return file;
    }

    /**
     * 同步文件上传（支持多文件+表单字段）
     * @param url          请求地址
     * @param headers      请求头
     * @param formParams   表单字段
     * @param fileParams   文件参数（key: 表单字段名, value: 文件对象）
     * @param mediaType    文件MIME类型（如 image/jpeg）
     */
    public static String uploadFiles(String url,
                                    @Nullable Map<String, String> headers,
                                    @Nullable Map<String, String> formParams,
                                    @NotNull Map<String, File> fileParams,
                                    @Nullable MediaType mediaType) throws IOException {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        // 添加表单字段
        if (formParams != null) {
            formParams.forEach(bodyBuilder::addFormDataPart);
        }

        // 添加文件
        for (Map.Entry<String, File> entry : fileParams.entrySet()) {
            String fieldName = entry.getKey();
            File file = entry.getValue();
            MediaType fileType = mediaType != null ? mediaType : MediaType.parse("application/octet-stream");
            bodyBuilder.addFormDataPart(
                    fieldName,
                    file.getName(),
                    RequestBody.create(file, fileType)
            );
        }

        Request request = buildRequest(url, "POST", headers, null, bodyBuilder.build());
        return executeAndParseString(request);
    }

    /**
     * 异步文件上传
     */
    public static void uploadFilesAsync(String url,
                                      @Nullable Map<String, String> headers,
                                      @Nullable Map<String, String> formParams,
                                      @NotNull Map<String, File> fileParams,
                                      @Nullable MediaType mediaType,
                                      OnResultCallback callback) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        if (formParams != null) {
            formParams.forEach(bodyBuilder::addFormDataPart);
        }

        for (Map.Entry<String, File> entry : fileParams.entrySet()) {
            String fieldName = entry.getKey();
            File file = entry.getValue();
            MediaType fileType = mediaType != null ? mediaType : MediaType.parse("application/octet-stream");
            bodyBuilder.addFormDataPart(
                    fieldName,
                    file.getName(),
                    RequestBody.create(file, fileType)
            );
        }

        Request request = buildRequest(url, "POST", headers, null, bodyBuilder.build());
        enqueue(request, callback);
    }

    //======================== 文件下载 ========================

    /**
     * 同步文件下载
     * @param url        文件URL
     * @param savePath   保存路径（包含文件名）
     */
    public static void downloadFileSync(String url, String savePath) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = execute(request)) {
            handleDownloadResponse(response, savePath);
        }
    }

    /**
     * 异步文件下载
     */
    public static void downloadFileAsync(String url, String savePath, OnDownloadCallback callback) {
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    handleDownloadResponse(response, savePath);
                    callback.onSuccess(savePath);
                } catch (IOException e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    private static void handleDownloadResponse(Response response, String savePath) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("下载失败，状态码: " + response.code());
        }

        ResponseBody body = response.body();
        if (body == null) {
            throw new IOException("响应体为空");
        }

        File file = new File(savePath);
        // 创建父目录（如果不存在）
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("创建父目录失败");
            }
        }

        try (BufferedSink sink = Okio.buffer(Okio.sink(file))) {
            sink.writeAll(body.source());
        }
    }

    //======================== 回调接口扩展 ========================

    public interface OnDownloadCallback {
        void onSuccess(String filePath);
        void onFailure(Exception e);
    }
}