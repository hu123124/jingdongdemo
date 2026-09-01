package com.example.jingdongdemo.service.impl;

import com.example.jingdongdemo.service.MinioService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.bucket}")
    private String bucket;

    /** 允许的图片格式白名单（按后缀判断） */
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp", "gif");

    /** 单张图片上限 5MB，和 application.properties 的 max-file-size 保持一致 */
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    @Override
    public String uploadImage(MultipartFile file) {
        // 1. 空文件校验
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 2. 大小校验（业务层第二道防线；第一道是 Spring 的 max-file-size）
        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("图片大小不能超过 5MB");
        }

        // 3. 类型校验：取原始文件名后缀，不在白名单直接拒绝
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new RuntimeException("仅支持 jpg/jpeg/png/webp/gif 格式的图片");
        }

        // 4. 生成对象名：UUID + 后缀 —— 防重名、防覆盖、防目录穿越
        String objectName = UUID.randomUUID() + "." + ext;

        try {
            // 5. 上传：流式写入 MinIO
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            log.info(">>> 图片上传成功: {}", objectName);
        } catch (Exception e) {
            log.error("图片上传 MinIO 失败", e);
            throw new RuntimeException("图片上传失败，请稍后重试");
        }

        // 6. 拼可访问 URL：endpoint + 桶名 + 对象名
        return endpoint + "/" + bucket + "/" + objectName;
    }

    /** 从文件名里取后缀（不含点），没有点返回空串 */
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}