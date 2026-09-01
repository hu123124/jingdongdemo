package com.example.jingdongdemo.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    /**
     * MinIO 客户端（线程安全，整个应用共享这一个）
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 应用启动后检查桶是否存在，不存在则创建（幂等，重复启动不会报错）
     */
    @Bean
    public ApplicationRunner minioBucketInit(MinioClient minioClient) {
        return args -> {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info(">>> MinIO 桶 [{}] 创建成功", bucket);
            } else {
                log.info(">>> MinIO 桶 [{}] 已存在，跳过创建", bucket);
            }
            // 设为公开读：任何人不带凭证都能下载桶里的对象（商品图就是要公开访问的）
            String policy = """
                    {"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["*"]},"Action":["s3:GetObject"],"Resource":["arn:aws:s3:::%s/*"]}]}
                    """.formatted(bucket);
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
            log.info(">>> MinIO 桶 [{}] 已设为公开读", bucket);
        };
    }
}