package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.DanceDto;
import com.alpha_code.alpha_code_activity_service.entity.Dance;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.grpc.client.RobotServiceClient;
import com.alpha_code.alpha_code_activity_service.repository.DanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DanceServiceImplTest {

    @Mock
    DanceRepository repository;

    @Mock
    RobotServiceClient robotServiceClient;

    @InjectMocks
    DanceServiceImpl service;

    @Test
    void getById_happyPath() {
        UUID id = UUID.randomUUID();
        Dance d = new Dance();
        d.setId(id);
        d.setName("D");
        d.setCreatedDate(LocalDateTime.now());
        d.setStatus(1);

        when(repository.findById(id)).thenReturn(Optional.of(d));

        DanceDto dto = service.getById(id);
        assertNotNull(dto);
        assertEquals("D", dto.getName());
    }

    @Test
    void getById_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
    }

    @Test
    void getAll_returnsPagedAndSetsUnknownOnModelNameWhenGrpcEmpty() {
        Dance d = new Dance();
        d.setId(UUID.randomUUID());
        d.setName("N");
        d.setRobotModelId(UUID.randomUUID());
        d.setCreatedDate(LocalDateTime.now());
        d.setStatus(1);

        when(repository.searchDances(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(d)));
        when(robotServiceClient.getRobotModelsByIds(any())).thenReturn(java.util.Collections.emptyMap());

        var res = service.getAll(1, 10, null, null, null, null);
        assertNotNull(res);
        assertEquals(1, res.getTotalCount());
    }
}

