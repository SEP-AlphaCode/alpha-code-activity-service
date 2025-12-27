package com.alpha_code.alpha_code_activity_service.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @Mock
    S3Client s3Client;

    @Mock
    Region region;

    @InjectMocks
    S3ServiceImpl s3Service;

    @Test
    void uploadBytes_returnsUrl() {
        when(region.id()).thenReturn("us-west-2");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        byte[] data = new byte[]{1,2,3};
        String url = s3Service.uploadBytes(data, "k.jpg", "image/jpg");

        assertNotNull(url);
        assertTrue(url.contains("k.jpg"));
    }
}

