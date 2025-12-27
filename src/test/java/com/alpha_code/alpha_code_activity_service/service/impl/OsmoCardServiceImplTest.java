package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.entity.OsmoCard;
import com.alpha_code.alpha_code_activity_service.repository.OsmoCardRepository;
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
class OsmoCardServiceImplTest {

    @Mock
    OsmoCardRepository repository;

    @InjectMocks
    OsmoCardServiceImpl service;

    @Test
    void getById_happyPath() {
        UUID id = UUID.randomUUID();
        OsmoCard o = new OsmoCard();
        o.setId(id);
        o.setName("OC");
        o.setCreatedDate(LocalDateTime.now());
        o.setStatus(1);

        when(repository.findById(id)).thenReturn(Optional.of(o));
        var dto = service.getById(id);
        assertNotNull(dto);
        assertEquals("OC", dto.getName());
    }

    @Test
    void getById_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getById(id));
    }
}

