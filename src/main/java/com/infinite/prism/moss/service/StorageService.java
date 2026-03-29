package com.infinite.prism.moss.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * 上传文件，返回可访问的 URL
     */
    String upload(MultipartFile file) throws Exception;

    /**
     * 上传返回可以公开访问的图片地址
     * @param file
     * @return 图片地址
     * @throws Exception
     */
    String openUpload(MultipartFile file) throws Exception;

    /**
     * 删除文件
     */
    void delete(String fileUrl) throws Exception;

    /**
     * 获取文件访问 URL（可能带过期时间）
     */
    String getFileUrl(String objectName, String bucketName) throws Exception;
}