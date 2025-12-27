package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.entity.ExtendedAction;
import com.alpha_code.alpha_code_activity_service.repository.ExtendedActionRepository;
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
class ExtendedActionServiceImplTest {

    @Mock
    ExtendedActionRepository repository;

    @InjectMocks
    ExtendedActionServiceImpl service;

    @Test
    void getById_happyPath() {
        UUID id = UUID.randomUUID();
        ExtendedAction e = new ExtendedAction();
        e.setId(id);
        e.setName("E");
        e.setCreatedDate(LocalDateTime.now());
        e.setStatus(1);

        when(repository.findById(id)).thenReturn(Optional.of(e));
        var dto = service.getExtendedActionById(id);
        assertNotNull(dto);
        assertEquals("E", dto.getName());
    }

    @Test
    void getById_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getExtendedActionById(id));
    }
}
