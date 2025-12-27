package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.ExpressionDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.entity.Expression;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.grpc.client.RobotServiceClient;
import com.alpha_code.alpha_code_activity_service.repository.ExpressionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import robot.Robot;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpressionServiceImplTest {

    @Mock
    ExpressionRepository repository;

    @Mock
    RobotServiceClient robotServiceClient;

    @InjectMocks
    ExpressionServiceImpl service;

    @Test
    void getAll_happyPath() {
        UUID robotModelId = UUID.randomUUID();
        Expression expression = new Expression();
        expression.setId(UUID.randomUUID());
        expression.setName("Happy");
        expression.setCode("HAPPY");
        expression.setRobotModelId(robotModelId);
        expression.setStatus(1);
        expression.setCreatedDate(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Expression> expressionPage = new PageImpl<>(List.of(expression), pageable, 1);

        when(repository.getAll(any(), any(), any(), any(), any())).thenReturn(expressionPage);
        when(robotServiceClient.getRobotModelsByIds(any())).thenReturn(Collections.emptyMap());

        PagedResult<ExpressionDto> result = service.getAll(1, 10, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        verify(repository).getAll(any(), any(), any(), any(), any());
    }

    @Test
    void getAll_emptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Expression> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.getAll(any(), any(), any(), any(), any())).thenReturn(emptyPage);

        PagedResult<ExpressionDto> result = service.getAll(1, 10, null, null, null, null);

        assertNotNull(result);
        assertEquals(0, result.getTotalCount());
    }

    @Test
    void getAll_withRobotModelName() {
        UUID robotModelId = UUID.randomUUID();
        Expression expression = new Expression();
        expression.setId(UUID.randomUUID());
        expression.setName("Happy");
        expression.setCode("HAPPY");
        expression.setRobotModelId(robotModelId);
        expression.setStatus(1);
        expression.setCreatedDate(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Expression> expressionPage = new PageImpl<>(List.of(expression), pageable, 1);

        Robot.RobotModelInformation modelInfo = Robot.RobotModelInformation.newBuilder()
                .setId(robotModelId.toString())
                .setName("Test Robot Model")
                .build();
        Map<String, Robot.RobotModelInformation> modelMap = Map.of(robotModelId.toString(), modelInfo);

        when(repository.getAll(any(), any(), any(), any(), any())).thenReturn(expressionPage);
        when(robotServiceClient.getRobotModelsByIds(any())).thenReturn(modelMap);

        PagedResult<ExpressionDto> result = service.getAll(1, 10, null, null, null, robotModelId);

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
    }

    @Test
    void getById_happyPath() {
        UUID id = UUID.randomUUID();
        Expression expression = new Expression();
        expression.setId(id);
        expression.setName("Happy");
        expression.setCode("HAPPY");
        expression.setStatus(1);
        expression.setCreatedDate(LocalDateTime.now());

        when(repository.findById(id)).thenReturn(Optional.of(expression));

        ExpressionDto result = service.getById(id);

        assertNotNull(result);
        assertEquals("Happy", result.getName());
        assertEquals("HAPPY", result.getCode());
        verify(repository).findById(id);
    }

    @Test
    void getById_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
        verify(repository).findById(id);
    }

    @Test
    void create_happyPath() {
        ExpressionDto dto = new ExpressionDto();
        dto.setName("Sad");
        dto.setCode("SAD");
        dto.setImageUrl("sad.png");
        dto.setStatus(1);
        dto.setRobotModelId(UUID.randomUUID());

        when(repository.getByNameIgnoreCaseAndStatusNot("Sad", 0))
                .thenReturn(Optional.empty());
        when(repository.getByCodeIgnoreCaseAndStatusNot("SAD", 0))
                .thenReturn(Optional.empty());

        Expression savedExpression = new Expression();
        savedExpression.setId(UUID.randomUUID());
        savedExpression.setName("Sad");
        savedExpression.setCode("SAD");
        savedExpression.setCreatedDate(LocalDateTime.now());

        when(repository.save(any(Expression.class))).thenReturn(savedExpression);

        ExpressionDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Sad", result.getName());
        verify(repository).getByNameIgnoreCaseAndStatusNot("Sad", 0);
        verify(repository).getByCodeIgnoreCaseAndStatusNot("SAD", 0);
        verify(repository).save(any(Expression.class));
    }

    @Test
    void create_conflictOnName() {
        ExpressionDto dto = new ExpressionDto();
        dto.setName("Existing Name");
        dto.setCode("NEW_CODE");

        when(repository.getByNameIgnoreCaseAndStatusNot("Existing Name", 0))
                .thenReturn(Optional.of(new Expression()));

        assertThrows(ResourceNotFoundException.class, () -> service.create(dto));
        verify(repository).getByNameIgnoreCaseAndStatusNot("Existing Name", 0);
        verify(repository, never()).save(any());
    }

    @Test
    void create_conflictOnCode() {
        ExpressionDto dto = new ExpressionDto();
        dto.setName("New Name");
        dto.setCode("EXISTING_CODE");

        when(repository.getByNameIgnoreCaseAndStatusNot("New Name", 0))
                .thenReturn(Optional.empty());
        when(repository.getByCodeIgnoreCaseAndStatusNot("EXISTING_CODE", 0))
                .thenReturn(Optional.of(new Expression()));

        assertThrows(ResourceNotFoundException.class, () -> service.create(dto));
        verify(repository).getByCodeIgnoreCaseAndStatusNot("EXISTING_CODE", 0);
        verify(repository, never()).save(any());
    }

    @Test
    void update_happyPath() {
        UUID id = UUID.randomUUID();
        Expression existingExpression = new Expression();
        existingExpression.setId(id);
        existingExpression.setName("Old Name");
        existingExpression.setCode("OLD_CODE");
        existingExpression.setStatus(1);

        ExpressionDto dto = new ExpressionDto();
        dto.setName("New Name");
        dto.setCode("NEW_CODE");
        dto.setImageUrl("new.png");
        dto.setStatus(2);
        dto.setRobotModelId(UUID.randomUUID());

        when(repository.findById(id)).thenReturn(Optional.of(existingExpression));
        when(repository.getByNameIgnoreCaseAndStatusNot("New Name", 0))
                .thenReturn(Optional.empty());
        when(repository.getByCodeIgnoreCaseAndStatusNot("NEW_CODE", 0))
                .thenReturn(Optional.empty());
        when(repository.save(any(Expression.class))).thenReturn(existingExpression);

        ExpressionDto result = service.update(id, dto);

        assertNotNull(result);
        verify(repository).findById(id);
        verify(repository).save(any(Expression.class));
    }

    @Test
    void update_notFound() {
        UUID id = UUID.randomUUID();
        ExpressionDto dto = new ExpressionDto();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(id, dto));
        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    void patch_happyPath() {
        UUID id = UUID.randomUUID();
        Expression existingExpression = new Expression();
        existingExpression.setId(id);
        existingExpression.setName("Old Name");
        existingExpression.setCode("OLD_CODE");
        existingExpression.setStatus(1);

        ExpressionDto dto = new ExpressionDto();
        dto.setName("New Name");

        when(repository.findById(id)).thenReturn(Optional.of(existingExpression));
        when(repository.getByNameIgnoreCaseAndStatusNot("New Name", 0))
                .thenReturn(Optional.empty());
        when(repository.save(any(Expression.class))).thenReturn(existingExpression);

        ExpressionDto result = service.patch(id, dto);

        assertNotNull(result);
        verify(repository).findById(id);
        verify(repository).save(any(Expression.class));
    }

    @Test
    void patch_partialUpdate() {
        UUID id = UUID.randomUUID();
        Expression existingExpression = new Expression();
        existingExpression.setId(id);
        existingExpression.setName("Old Name");
        existingExpression.setCode("OLD_CODE");
        existingExpression.setStatus(1);

        ExpressionDto dto = new ExpressionDto();
        dto.setImageUrl("new_image.png");

        when(repository.findById(id)).thenReturn(Optional.of(existingExpression));
        when(repository.save(any(Expression.class))).thenReturn(existingExpression);

        ExpressionDto result = service.patch(id, dto);

        assertNotNull(result);
        verify(repository).findById(id);
        verify(repository).save(any(Expression.class));
    }

    @Test
    void patch_notFound() {
        UUID id = UUID.randomUUID();
        ExpressionDto dto = new ExpressionDto();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.patch(id, dto));
        verify(repository, never()).save(any());
    }

    @Test
    void delete_happyPath() {
        UUID id = UUID.randomUUID();
        Expression existingExpression = new Expression();
        existingExpression.setId(id);
        existingExpression.setStatus(1);

        when(repository.findById(id)).thenReturn(Optional.of(existingExpression));
        when(repository.save(any(Expression.class))).thenReturn(existingExpression);

        String result = service.delete(id);

        assertNotNull(result);
        assertTrue(result.contains("deleted successfully"));
        verify(repository).findById(id);
        verify(repository).save(any(Expression.class));
    }

    @Test
    void delete_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
        verify(repository, never()).save(any());
    }

    @Test
    void getByName_happyPath() {
        String name = "Happy";
        Expression expression = new Expression();
        expression.setId(UUID.randomUUID());
        expression.setName(name);
        expression.setCode("HAPPY");
        expression.setStatus(1);

        when(repository.getByNameIgnoreCaseAndStatusNot(name, 0)).thenReturn(Optional.of(expression));

        ExpressionDto result = service.getByName(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(repository).getByNameIgnoreCaseAndStatusNot(name, 0);
    }

    @Test
    void getByName_notFound() {
        String name = "Non-existent";
        when(repository.getByNameIgnoreCaseAndStatusNot(name, 0)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getByName(name));
    }

    @Test
    void getByCode_happyPath() {
        String code = "HAPPY";
        Expression expression = new Expression();
        expression.setId(UUID.randomUUID());
        expression.setName("Happy");
        expression.setCode(code);
        expression.setStatus(1);

        when(repository.getByCodeIgnoreCaseAndStatusNot(code, 0)).thenReturn(Optional.of(expression));

        ExpressionDto result = service.getByCode(code);

        assertNotNull(result);
        assertEquals(code, result.getCode());
        verify(repository).getByCodeIgnoreCaseAndStatusNot(code, 0);
    }

    @Test
    void getByCode_notFound() {
        String code = "NON_EXISTENT";
        when(repository.getByCodeIgnoreCaseAndStatusNot(code, 0)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getByCode(code));
    }

    @Test
    void getByRobotModelId_happyPath() {
        UUID robotModelId = UUID.randomUUID();
        Expression expression1 = new Expression();
        expression1.setId(UUID.randomUUID());
        expression1.setName("Happy");
        expression1.setRobotModelId(robotModelId);
        expression1.setStatus(1);

        Expression expression2 = new Expression();
        expression2.setId(UUID.randomUUID());
        expression2.setName("Sad");
        expression2.setRobotModelId(robotModelId);
        expression2.setStatus(1);

        when(repository.getByRobotModelIdAndStatusNot(robotModelId, 0))
                .thenReturn(List.of(expression1, expression2));

        List<ExpressionDto> result = service.getByRobotModelId(robotModelId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).getByRobotModelIdAndStatusNot(robotModelId, 0);
    }

    @Test
    void changeStatus_happyPath() {
        UUID id = UUID.randomUUID();
        Expression existingExpression = new Expression();
        existingExpression.setId(id);
        existingExpression.setStatus(1);

        when(repository.findById(id)).thenReturn(Optional.of(existingExpression));
        when(repository.save(any(Expression.class))).thenReturn(existingExpression);

        ExpressionDto result = service.changeStatus(id, 2);

        assertNotNull(result);
        verify(repository).findById(id);
        verify(repository).save(any(Expression.class));
    }

    @Test
    void changeStatus_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.changeStatus(id, 2));
        verify(repository, never()).save(any());
    }

    @Test
    void getAll_grpcException() {
        UUID robotModelId = UUID.randomUUID();
        Expression expression = new Expression();
        expression.setId(UUID.randomUUID());
        expression.setName("Happy");
        expression.setRobotModelId(robotModelId);
        expression.setStatus(1);
        expression.setCreatedDate(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Expression> expressionPage = new PageImpl<>(List.of(expression), pageable, 1);

        when(repository.getAll(any(), any(), any(), any(), any())).thenReturn(expressionPage);
        when(robotServiceClient.getRobotModelsByIds(any()))
                .thenThrow(new RuntimeException("gRPC error"));

        PagedResult<ExpressionDto> result = service.getAll(1, 10, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
    }
}
