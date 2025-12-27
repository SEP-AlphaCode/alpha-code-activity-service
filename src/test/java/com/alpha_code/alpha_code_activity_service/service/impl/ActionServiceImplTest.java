package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.ActionDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.entity.Action;
import com.alpha_code.alpha_code_activity_service.exception.ConflictException;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.grpc.client.RobotServiceClient;
import com.alpha_code.alpha_code_activity_service.repository.ActionRepository;
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
class ActionServiceImplTest {

    @Mock
    ActionRepository actionRepository;

    @Mock
    RobotServiceClient robotServiceClient;

    @InjectMocks
    ActionServiceImpl service;

    @Test
    void searchActions_happyPath() {
        UUID robotModelId = UUID.randomUUID();
        Action action = new Action();
        action.setId(UUID.randomUUID());
        action.setName("Test Action");
        action.setCode("TEST_ACTION");
        action.setRobotModelId(robotModelId);
        action.setStatus(1);
        action.setCreatedDate(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Action> actionPage = new PageImpl<>(List.of(action), pageable, 1);

        when(actionRepository.searchActions(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(actionPage);
        when(robotServiceClient.getRobotModelsByIds(any())).thenReturn(Collections.emptyMap());

        PagedResult<ActionDto> result = service.searchActions(1, 10, robotModelId, null, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        verify(actionRepository).searchActions(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchActions_emptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Action> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(actionRepository.searchActions(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(emptyPage);

        PagedResult<ActionDto> result = service.searchActions(1, 10, null, null, null, null, null, null);

        assertNotNull(result);
        assertEquals(0, result.getTotalCount());
    }

    @Test
    void searchActions_withRobotModelName() {
        UUID robotModelId = UUID.randomUUID();
        Action action = new Action();
        action.setId(UUID.randomUUID());
        action.setName("Test Action");
        action.setCode("TEST_ACTION");
        action.setRobotModelId(robotModelId);
        action.setStatus(1);
        action.setCreatedDate(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Action> actionPage = new PageImpl<>(List.of(action), pageable, 1);

        Robot.RobotModelInformation modelInfo = Robot.RobotModelInformation.newBuilder()
                .setId(robotModelId.toString())
                .setName("Test Robot Model")
                .build();
        Map<String, Robot.RobotModelInformation> modelMap = Map.of(robotModelId.toString(), modelInfo);

        when(actionRepository.searchActions(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(actionPage);
        when(robotServiceClient.getRobotModelsByIds(any())).thenReturn(modelMap);

        PagedResult<ActionDto> result = service.searchActions(1, 10, robotModelId, null, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
    }

    @Test
    void getActionById_happyPath() {
        UUID id = UUID.randomUUID();
        Action action = new Action();
        action.setId(id);
        action.setName("Test Action");
        action.setCode("TEST_ACTION");
        action.setStatus(1);
        action.setCreatedDate(LocalDateTime.now());

        when(actionRepository.findById(id)).thenReturn(Optional.of(action));

        ActionDto result = service.getActionById(id);

        assertNotNull(result);
        assertEquals("Test Action", result.getName());
        assertEquals("TEST_ACTION", result.getCode());
        verify(actionRepository).findById(id);
    }

    @Test
    void getActionById_notFound() {
        UUID id = UUID.randomUUID();
        when(actionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getActionById(id));
        verify(actionRepository).findById(id);
    }

    @Test
    void getActionByName_happyPath() {
        String name = "Test Action";
        Action action = new Action();
        action.setId(UUID.randomUUID());
        action.setName(name);
        action.setCode("TEST_ACTION");
        action.setStatus(1);

        when(actionRepository.findByNameIgnoreCaseAndStatusNot(name, 0)).thenReturn(Optional.of(action));

        ActionDto result = service.getActionByName(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(actionRepository).findByNameIgnoreCaseAndStatusNot(name, 0);
    }

    @Test
    void getActionByName_notFound() {
        String name = "Non-existent Action";
        when(actionRepository.findByNameIgnoreCaseAndStatusNot(name, 0)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getActionByName(name));
    }

    @Test
    void getActionByCode_happyPath() {
        String code = "TEST_ACTION";
        Action action = new Action();
        action.setId(UUID.randomUUID());
        action.setName("Test Action");
        action.setCode(code);
        action.setStatus(1);

        when(actionRepository.findByCodeIgnoreCaseAndStatusNot(code, 0)).thenReturn(Optional.of(action));

        ActionDto result = service.getActionByCode(code);

        assertNotNull(result);
        assertEquals(code, result.getCode());
        verify(actionRepository).findByCodeIgnoreCaseAndStatusNot(code, 0);
    }

    @Test
    void getActionByCode_notFound() {
        String code = "NON_EXISTENT";
        when(actionRepository.findByCodeIgnoreCaseAndStatusNot(code, 0)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getActionByCode(code));
    }

    @Test
    void getActionByRobotModelId_happyPath() {
        UUID robotModelId = UUID.randomUUID();
        Action action = new Action();
        action.setId(UUID.randomUUID());
        action.setName("Test Action");
        action.setRobotModelId(robotModelId);
        action.setStatus(1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Action> actionPage = new PageImpl<>(List.of(action), pageable, 1);

        when(actionRepository.findByRobotModelIdAndStatusNot(robotModelId, 0, pageable))
                .thenReturn(actionPage);

        PagedResult<ActionDto> result = service.getActionByRobotModelId(robotModelId, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        verify(actionRepository).findByRobotModelIdAndStatusNot(robotModelId, 0, pageable);
    }

    @Test
    void createAction_happyPath() {
        ActionDto actionDto = new ActionDto();
        actionDto.setName("New Action");
        actionDto.setCode("NEW_ACTION");
        actionDto.setStatus(1);
        actionDto.setDuration(10);
        actionDto.setCanInterrupt(false);
        actionDto.setRobotModelId(UUID.randomUUID());
        actionDto.setIcon("icon.png");
        actionDto.setType(1);

        when(actionRepository.findByCodeIgnoreCaseAndStatusNot("NEW_ACTION", 0))
                .thenReturn(Optional.empty());
        when(actionRepository.findByNameIgnoreCaseAndStatusNot("New Action", 0))
                .thenReturn(Optional.empty());

        Action savedAction = new Action();
        savedAction.setId(UUID.randomUUID());
        savedAction.setName("New Action");
        savedAction.setCode("NEW_ACTION");
        savedAction.setStatus(1);
        savedAction.setCreatedDate(LocalDateTime.now());

        when(actionRepository.save(any(Action.class))).thenReturn(savedAction);

        ActionDto result = service.createAction(actionDto);

        assertNotNull(result);
        assertEquals("New Action", result.getName());
        verify(actionRepository).findByCodeIgnoreCaseAndStatusNot("NEW_ACTION", 0);
        verify(actionRepository).findByNameIgnoreCaseAndStatusNot("New Action", 0);
        verify(actionRepository).save(any(Action.class));
    }

    @Test
    void createAction_conflictOnCode() {
        ActionDto actionDto = new ActionDto();
        actionDto.setName("New Action");
        actionDto.setCode("EXISTING_CODE");

        Action existingAction = new Action();
        when(actionRepository.findByCodeIgnoreCaseAndStatusNot("EXISTING_CODE", 0))
                .thenReturn(Optional.of(existingAction));

        assertThrows(ConflictException.class, () -> service.createAction(actionDto));
        verify(actionRepository).findByCodeIgnoreCaseAndStatusNot("EXISTING_CODE", 0);
        verify(actionRepository, never()).save(any());
    }

    @Test
    void createAction_conflictOnName() {
        ActionDto actionDto = new ActionDto();
        actionDto.setName("Existing Name");
        actionDto.setCode("NEW_CODE");

        when(actionRepository.findByCodeIgnoreCaseAndStatusNot("NEW_CODE", 0))
                .thenReturn(Optional.empty());
        when(actionRepository.findByNameIgnoreCaseAndStatusNot("Existing Name", 0))
                .thenReturn(Optional.of(new Action()));

        assertThrows(ConflictException.class, () -> service.createAction(actionDto));
        verify(actionRepository).findByCodeIgnoreCaseAndStatusNot("NEW_CODE", 0);
        verify(actionRepository).findByNameIgnoreCaseAndStatusNot("Existing Name", 0);
        verify(actionRepository, never()).save(any());
    }

    @Test
    void updateAction_happyPath() {
        UUID id = UUID.randomUUID();
        Action existingAction = new Action();
        existingAction.setId(id);
        existingAction.setName("Old Name");
        existingAction.setCode("OLD_CODE");
        existingAction.setStatus(1);

        ActionDto actionDto = new ActionDto();
        actionDto.setName("New Name");
        actionDto.setDescription("New Description");
        actionDto.setStatus(2);
        actionDto.setDuration(20);
        actionDto.setCanInterrupt(true);
        actionDto.setRobotModelId(UUID.randomUUID());
        actionDto.setIcon("new_icon.png");
        actionDto.setType(2);

        when(actionRepository.findById(id)).thenReturn(Optional.of(existingAction));
        when(actionRepository.save(any(Action.class))).thenReturn(existingAction);

        ActionDto result = service.updateAction(id, actionDto);

        assertNotNull(result);
        verify(actionRepository).findById(id);
        verify(actionRepository).save(any(Action.class));
    }

    @Test
    void updateAction_notFound() {
        UUID id = UUID.randomUUID();
        ActionDto actionDto = new ActionDto();

        when(actionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateAction(id, actionDto));
        verify(actionRepository).findById(id);
        verify(actionRepository, never()).save(any());
    }

    @Test
    void patchUpdateAction_happyPath() {
        UUID id = UUID.randomUUID();
        Action existingAction = new Action();
        existingAction.setId(id);
        existingAction.setName("Old Name");
        existingAction.setStatus(1);

        ActionDto actionDto = new ActionDto();
        actionDto.setName("New Name");

        when(actionRepository.findById(id)).thenReturn(Optional.of(existingAction));
        when(actionRepository.save(any(Action.class))).thenReturn(existingAction);

        ActionDto result = service.patchUpdateAction(id, actionDto);

        assertNotNull(result);
        verify(actionRepository).findById(id);
        verify(actionRepository).save(any(Action.class));
    }

    @Test
    void patchUpdateAction_partialUpdate() {
        UUID id = UUID.randomUUID();
        Action existingAction = new Action();
        existingAction.setId(id);
        existingAction.setName("Old Name");
        existingAction.setDescription("Old Description");
        existingAction.setStatus(1);
        existingAction.setDuration(10);

        ActionDto actionDto = new ActionDto();
        actionDto.setDescription("New Description");

        when(actionRepository.findById(id)).thenReturn(Optional.of(existingAction));
        when(actionRepository.save(any(Action.class))).thenReturn(existingAction);

        ActionDto result = service.patchUpdateAction(id, actionDto);

        assertNotNull(result);
        verify(actionRepository).findById(id);
        verify(actionRepository).save(any(Action.class));
    }

    @Test
    void patchUpdateAction_notFound() {
        UUID id = UUID.randomUUID();
        ActionDto actionDto = new ActionDto();

        when(actionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.patchUpdateAction(id, actionDto));
    }

    @Test
    void changeActionStatus_happyPath() {
        UUID id = UUID.randomUUID();
        Action existingAction = new Action();
        existingAction.setId(id);
        existingAction.setStatus(1);

        when(actionRepository.findById(id)).thenReturn(Optional.of(existingAction));
        when(actionRepository.save(any(Action.class))).thenReturn(existingAction);

        ActionDto result = service.changeActionStatus(id, 2);

        assertNotNull(result);
        verify(actionRepository).findById(id);
        verify(actionRepository).save(any(Action.class));
    }

    @Test
    void changeActionStatus_notFound() {
        UUID id = UUID.randomUUID();
        when(actionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.changeActionStatus(id, 2));
    }

    @Test
    void deleteAction_happyPath() {
        UUID id = UUID.randomUUID();
        Action existingAction = new Action();
        existingAction.setId(id);
        existingAction.setStatus(1);

        when(actionRepository.findById(id)).thenReturn(Optional.of(existingAction));
        when(actionRepository.save(any(Action.class))).thenReturn(existingAction);

        String result = service.deleteAction(id);

        assertNotNull(result);
        assertTrue(result.contains("xóa thành công"));
        verify(actionRepository).findById(id);
        verify(actionRepository).save(any(Action.class));
    }

    @Test
    void deleteAction_notFound() {
        UUID id = UUID.randomUUID();
        when(actionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteAction(id));
        verify(actionRepository, never()).save(any());
    }

    @Test
    void searchActions_grpcException() {
        UUID robotModelId = UUID.randomUUID();
        Action action = new Action();
        action.setId(UUID.randomUUID());
        action.setName("Test Action");
        action.setCode("TEST_ACTION");
        action.setRobotModelId(robotModelId);
        action.setStatus(1);
        action.setCreatedDate(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Action> actionPage = new PageImpl<>(List.of(action), pageable, 1);

        when(actionRepository.searchActions(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(actionPage);
        when(robotServiceClient.getRobotModelsByIds(any()))
                .thenThrow(new RuntimeException("gRPC error"));

        PagedResult<ActionDto> result = service.searchActions(1, 10, robotModelId, null, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
    }
}
