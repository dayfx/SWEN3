package com.fhtechnikum.paperless.services.impl;

import com.fhtechnikum.paperless.config.MinIOConfig;
import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinIOFileStorageTest {

    @Mock
    private MinIOConfig minIOConfig;

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinIOFileStorage fileStorage;

    @BeforeEach
    void setUp() {
        // lenient() because some tests might not reach this call
        lenient().when(minIOConfig.getBucketName()).thenReturn("test-bucket");
    }

    @Test
    void upload_ShouldCreateBucket_IfItDoesNotExist() throws Exception {

        String objectName = "test.pdf";
        byte[] content = "Hello".getBytes();

        // simulate bucket does NOT exist
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        fileStorage.upload(objectName, content);

        // verify we tried to create the bucket
        verify(minioClient, times(1)).makeBucket(any(MakeBucketArgs.class));

        // verify we tried to upload the file
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void upload_ShouldSkipBucketCreation_IfItExists() throws Exception {

        String objectName = "test.pdf";
        byte[] content = "Hello".getBytes();

        // simulate bucket DOES exist
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        fileStorage.upload(objectName, content);

        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void upload_ShouldThrowRuntimeException_OnMinioError() throws Exception {

        String objectName = "test.pdf";
        byte[] content = "Hello".getBytes();

        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenThrow(new RuntimeException("Connection failed"));

        assertThrows(RuntimeException.class, () -> {
            fileStorage.upload(objectName, content);
        });
    }

    @Test
    void delete_ShouldCallRemoveObject() throws Exception {

        String objectName = "file-to-delete.pdf";

        fileStorage.delete(objectName);

        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }
}