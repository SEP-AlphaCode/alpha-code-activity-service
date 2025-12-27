package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.ActivityDto;
import com.alpha_code.alpha_code_activity_service.entity.Activity;
import com.alpha_code.alpha_code_activity_service.exception.ConflictException;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.repository.ActivityRepository;
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
class ActivityServiceImplTest {

    @Mock
    ActivityRepository repository;

    @InjectMocks
    ActivityServiceImpl service;

    @Test
    void getActivityById_happyPath() {
        UUID id = UUID.randomUUID();
        Activity a = new Activity();
        a.setId(id);
        a.setName("Act");
        a.setCreatedDate(LocalDateTime.now());
        a.setStatus(1);

        when(repository.findById(id)).thenReturn(Optional.of(a));

        ActivityDto dto = service.getActivityById(id);

        assertNotNull(dto);
        assertEquals("Act", dto.getName());
    }

    @Test
    void createActivity_conflictOnName() {
        ActivityDto dto = new ActivityDto();
        dto.setName("X");
        dto.setRobotModelId(UUID.randomUUID());

        when(repository.findByNameIgnoreCaseAndRobotModelIdAndStatusNot(dto.getName(), dto.getRobotModelId(), 0))
                .thenReturn(Optional.of(new Activity()));

        assertThrows(ConflictException.class, () -> service.createActivity(dto));
    }

    @Test
    void getAll_returnsPaged() {
        Activity a = new Activity();
        a.setId(UUID.randomUUID());
        a.setName("A");
        a.setCreatedDate(LocalDateTime.now());
        a.setStatus(1);

        when(repository.searchActivities(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(a)));

        var result = service.getAll(1, 10, null, null, null, null);
        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
    }
}

