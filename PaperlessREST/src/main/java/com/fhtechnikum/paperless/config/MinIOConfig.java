package com.fhtechnikum.paperless.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOConfig {

    @Value("${minio.bucket-name:documents}")
    private String bucketName;

    @Value("${minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${minio.secret-key:minioadmin}")
    private String secretKey;

    @Value("${minio.url:http://localhost:9000}")
    private String minioUrl;

    public String getBucketName() {
        return bucketName;
    }

    @Bean
    public MinioClient generateMinioClient() {
        try {
            return MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(accessKey, secretKey)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create MinIO client: " + e.getMessage(), e);
        }
    }
}
