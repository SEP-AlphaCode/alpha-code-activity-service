package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.entity.QrCode;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.repository.QrCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QrCodeServiceImplTest {

    @Mock
    QrCodeRepository repository;

    @InjectMocks
    QrCodeServiceImpl service;

    @Test
    void getById_happyPath() {
        UUID id = UUID.randomUUID();
        QrCode q = new QrCode();
        q.setId(id);
        q.setQrCode("C");
        q.setCreatedDate(LocalDateTime.now());
        q.setStatus(1);

        when(repository.findById(id)).thenReturn(Optional.of(q));

        var dto = service.getById(id);
        assertNotNull(dto);
        assertEquals("C", dto.getQrCode());
    }

    @Test
    void getById_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
    }
}

