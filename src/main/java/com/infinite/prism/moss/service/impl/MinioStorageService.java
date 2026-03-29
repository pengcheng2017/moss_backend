package com.infinite.prism.moss.service.impl;

import com.infinite.prism.moss.config.MinioConfig;
import com.infinite.prism.moss.service.StorageService;
import com.infinite.prism.moss.utils.HttpClientUtil;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public String upload(MultipartFile file) throws Exception {

        // 1. 检查存储桶是否存在，不存在则创建
        String bucketName = minioConfig.getBucketName();
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("创建存储桶: {}", bucketName);
        }

        // 2. 生成唯一文件名（保留原始扩展名）
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectName = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 3. 上传文件
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build();
        minioClient.putObject(args);

        log.info("文件上传成功: bucket={}, object={}", bucketName, objectName);

        // 4. 返回文件的访问 URL（这里直接拼接，也可以使用 getFileUrl 方法生成预签名 URL）
        return getFileUrl(objectName, bucketName);
    }

    @Override
    public String openUpload(MultipartFile file) throws Exception {
        // 1. 校验文件是否为空
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 2. 生成唯一的对象名（保留原始文件扩展名）
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectName = UUID.randomUUID().toString() + extension; // 例如：550e8400-e29b-41d4-a716-446655440000.jpg

        // 3. 定义存储桶名称（可配置，这里直接使用 "pic"）
        String bucketName = "pic";

        // 4. 确保存储桶存在（生产环境可预先创建，此处为了健壮性做检查）
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // 创建桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

                // 设置桶策略为公开读（允许所有用户列出和读取桶内对象）
                String policyJson = "{\n" +
                        "  \"Version\": \"2012-10-17\",\n" +
                        "  \"Statement\": [\n" +
                        "    {\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                        "      \"Action\": [\"s3:GetObject\"],\n" +
                        "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(bucketName)
                                .config(policyJson)
                                .build()
                );
            }
        } catch (MinioException e) {
            throw new RuntimeException("检查或创建存储桶失败", e);
        }

        // 5. 上传文件到 MinIO
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)  // 使用文件实际大小
                    .contentType(file.getContentType())      // 设置正确的 Content-Type
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("文件上传到 MinIO 失败", e);
        }
        String fileUrl = getFileUrl(objectName, bucketName);
        if (fileUrl != null) {
            return fileUrl.substring(0, fileUrl.indexOf("?"));
        }
        throw new RuntimeException("failed to upload");
    }

    @Override
    public void delete(String fileUrl) throws Exception {
        // 从 URL 中解析 objectName（根据你的 URL 格式实现）
        // 这里简单实现，假设 URL 格式为 http://endpoint/bucket/objectName
        // 1. 检查存储桶是否存在，不存在则创建
        String objectName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minioConfig.getBucketName())
                .object(objectName)
                .build());
        log.info("文件删除成功: {}", objectName);
    }

    @Override
    public String getFileUrl(String objectName, String bucketName) throws Exception {
        // 生成一个有效期为 7 天的预签名 URL
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(7, TimeUnit.DAYS)
                        .build()
        );
        log.info("minio file url is {}", url);
        return url;
    }

    /**
     * 从第三方URL下载图片并上传到MinIO，返回7天后过期的预签名URL
     *
     * @param sourceUrl  第三方图片URL
     * @param bucketName MinIO桶名称
     * @param objectName MinIO对象名称（包含路径，如 "images/2025/03/abc.jpg"）
     * @return 预签名URL（7天有效期）
     * @throws IOException 当下载或上传失败时抛出
     */
    public String uploadFromUrlAndGetPresignedUrl(String sourceUrl, String bucketName, String objectName) throws IOException {
        // 1. 下载图片流
        log.info("开始从第三方URL下载图片: {}", sourceUrl);
        try (Response response = HttpClientUtil.newCall(new Request.Builder().url(sourceUrl).build())) {

            // 1. 检查存储桶是否存在，不存在则创建
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("创建存储桶: {}", bucketName);
            }

            if (!response.isSuccessful()) {
                throw new IOException("下载图片失败，HTTP状态码: " + response.code());
            }
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("响应体为空");
            }

            // 获取内容类型和大小（可选，用于上传优化）
            String contentType = response.header("Content-Type");
            long contentLength = body.contentLength(); // 可能为-1，未知长度

            // 2. 上传到MinIO
            log.info("开始上传到MinIO, bucket: {}, object: {}", bucketName, objectName);
            try (InputStream inputStream = body.byteStream()) {
                PutObjectArgs putArgs = PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, contentLength, -1) // 如果长度未知，设置-1使用分块上传
                        .contentType(contentType)
                        .build();
                minioClient.putObject(putArgs);
            }

            // 3. 生成7天后过期的预签名GET URL
            Map<String, String> reqParams = new HashMap<>(); // 可添加额外请求参数，如响应内容 disposition
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry((int) Duration.ofDays(7).getSeconds()) // 有效期7天
                            .extraQueryParams(reqParams)
                            .build()
            );

            log.info("预签名URL生成成功: {}", presignedUrl);
            return presignedUrl;
        } catch (Exception e) {
            log.error("处理图片失败: {}", e.getMessage(), e);
            throw new IOException("从第三方地址上传至MinIO失败", e);
        }
    }
}