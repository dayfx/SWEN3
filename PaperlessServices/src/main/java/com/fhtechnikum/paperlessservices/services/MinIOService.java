package com.fhtechnikum.paperlessservices.services;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Service for downloading files from MinIO object storage
 */
@Service
public class MinIOService {

    private static final Logger log = LoggerFactory.getLogger(MinIOService.class);

    private final MinioClient minioClient;

    @Value("${minio.bucket-name:documents}")
    private String bucketName;

    public MinIOService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * Download a file from MinIO
     *
     * @param objectKey The MinIO object key
     * @return File content as byte array
     * @throws Exception if download fails
     */
    public byte[] downloadFile(String objectKey) throws Exception {
        log.info("Downloading file from MinIO: bucket={}, key={}", bucketName, objectKey);

        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectKey)
                        .build()
        )) {
            byte[] data = stream.readAllBytes();
            log.info("Downloaded file from MinIO: size={} bytes", data.length);
            return data;
        } catch (Exception e) {
            log.error("Failed to download file from MinIO: bucket={}, key={}, error={}",
                     bucketName, objectKey, e.getMessage(), e);
            throw e;
        }
    }
}
