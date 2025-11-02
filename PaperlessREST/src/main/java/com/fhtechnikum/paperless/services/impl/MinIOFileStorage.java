package com.fhtechnikum.paperless.services.impl;

import com.fhtechnikum.paperless.config.MinIOConfig;
import com.fhtechnikum.paperless.services.FileStorage;
import io.minio.*;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinIOFileStorage implements FileStorage {

    private static final Logger log = LoggerFactory.getLogger(MinIOFileStorage.class);

    private final MinIOConfig minIOConfig;
    private final MinioClient minioClient;

    @Autowired
    public MinIOFileStorage(MinIOConfig minIOConfig, MinioClient minioClient) {
        this.minIOConfig = minIOConfig;
        this.minioClient = minioClient;
    }

    @Override
    public void upload(String objectName, byte[] file) {
        try {
            // Check if bucket exists, create if not
            boolean hasBucketWithName = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .build()
            );

            if (!hasBucketWithName) {
                log.info("Creating MinIO bucket: {}", minIOConfig.getBucketName());
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minIOConfig.getBucketName())
                                .build()
                );
            }

            // Upload file to MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .object(objectName)
                            .stream(new ByteArrayInputStream(file), file.length, -1)
                            .build()
            );

            log.info("Uploaded file to MinIO: bucket={}, key={}, size={} bytes",
                    minIOConfig.getBucketName(), objectName, file.length);

        } catch (MinioException e) {
            log.error("MinIO error occurred: {}", e.getMessage());
            log.error("HTTP trace: {}", e.httpTrace());
            throw new RuntimeException("Failed to upload file to MinIO", e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error uploading file to MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    @Override
    public byte[] download(String objectName) {
        try {
            log.info("Downloading file from MinIO: bucket={}, key={}",
                    minIOConfig.getBucketName(), objectName);

            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .object(objectName)
                            .build())) {

                byte[] data = stream.readAllBytes();
                log.info("Downloaded {} bytes from MinIO", data.length);
                return data;
            }

        } catch (ServerException | InvalidResponseException | InsufficientDataException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | ErrorResponseException | XmlParserException |
                 InternalException e) {
            log.error("Error downloading file from MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download file from MinIO", e);
        }
    }

    @Override
    public void delete(String objectName) {
        try {
            log.info("Deleting file from MinIO: bucket={}, key={}",
                    minIOConfig.getBucketName(), objectName);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .object(objectName)
                            .build()
            );

            log.info("Successfully deleted file from MinIO: bucket={}, key={}",
                    minIOConfig.getBucketName(), objectName);

        } catch (ServerException | InvalidResponseException | InsufficientDataException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | ErrorResponseException | XmlParserException |
                 InternalException e) {
            log.error("Error deleting file from MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file from MinIO", e);
        }
    }
}
