package com.fhtechnikum.paperlessservices.services;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinIOServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private GetObjectResponse getObjectResponse; // mock the file stream

    @InjectMocks
    private MinIOService minIOService;

    @BeforeEach
    void setUp() {
        // inject the bucket name since @Value doesn't run in unit tests
        ReflectionTestUtils.setField(minIOService, "bucketName", "test-bucket");
    }

    @Test
    void downloadFile_ShouldThrowException_WhenMinioFails() throws Exception {

        String objectKey = "broken.pdf";

        // force MinIO to throw an exception
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException("MinIO is down"));

        assertThrows(Exception.class, () -> {
            minIOService.downloadFile(objectKey);
        });
    }
}