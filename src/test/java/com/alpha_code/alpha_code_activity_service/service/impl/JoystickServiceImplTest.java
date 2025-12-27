package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.ActionDto;
import com.alpha_code.alpha_code_activity_service.dto.DanceDto;
import com.alpha_code.alpha_code_activity_service.dto.ExpressionDto;
import com.alpha_code.alpha_code_activity_service.dto.SkillDto;
import com.alpha_code.alpha_code_activity_service.dto.JoystickDto;
import com.alpha_code.alpha_code_activity_service.entity.Joystick;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.repository.JoystickRepository;
import com.alpha_code.alpha_code_activity_service.service.ActionService;
import com.alpha_code.alpha_code_activity_service.service.DanceService;
import com.alpha_code.alpha_code_activity_service.service.ExpressionService;
import com.alpha_code.alpha_code_activity_service.service.ExtendedActionService;
import com.alpha_code.alpha_code_activity_service.service.SkillService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoystickServiceImplTest {

    @Mock
    JoystickRepository joystickRepository;

    @Mock
    ActionService actionService;

    @Mock
    DanceService danceService;

    @Mock
    ExpressionService expressionService;

    @Mock
    SkillService skillService;

    @Mock
    ExtendedActionService extendedActionService;

    @InjectMocks
    JoystickServiceImpl service;

    @Test
    void getByAccountIdAndRobotId_happyPath() {
        UUID accountId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();

        Joystick joystick1 = new Joystick();
        joystick1.setId(UUID.randomUUID());
        joystick1.setAccountId(accountId);
        joystick1.setRobotId(robotId);
        joystick1.setButtonCode("BUTTON_A");
        joystick1.setStatus(1);

        Joystick joystick2 = new Joystick();
        joystick2.setId(UUID.randomUUID());
        joystick2.setAccountId(accountId);
        joystick2.setRobotId(robotId);
        joystick2.setButtonCode("BUTTON_B");
        joystick2.setStatus(1);

        when(joystickRepository.findListByAccountIdAndRobotIdAndStatus(accountId, robotId, 1))
                .thenReturn(List.of(joystick1, joystick2));

        List<JoystickDto> result = service.getByAccountIdAndRobotId(accountId, robotId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(joystickRepository).findListByAccountIdAndRobotIdAndStatus(accountId, robotId, 1);
    }

    @Test
    void getByAccountIdAndRobotId_notFound() {
        UUID accountId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();

        when(joystickRepository.findListByAccountIdAndRobotIdAndStatus(accountId, robotId, 1))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, 
                () -> service.getByAccountIdAndRobotId(accountId, robotId));
        verify(joystickRepository).findListByAccountIdAndRobotIdAndStatus(accountId, robotId, 1);
    }

    @Test
    void create_happyPath_withAction() {
        UUID accountId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();

        JoystickDto dto = new JoystickDto();
        dto.setAccountId(accountId);
        dto.setRobotId(robotId);
        dto.setButtonCode("BUTTON_A");
        dto.setType("TYPE_A");
        dto.setActionId(actionId);

        when(joystickRepository.findByAccountIdAndRobotIdAndButtonCodeAndStatus(
                accountId, robotId, "BUTTON_A", 1))
                .thenReturn(Optional.empty());
        
        ActionDto actionDto = new ActionDto();
        when(actionService.getActionById(actionId)).thenReturn(actionDto);

        Joystick savedJoystick = new Joystick();
        savedJoystick.setId(UUID.randomUUID());
        savedJoystick.setAccountId(accountId);
        savedJoystick.setRobotId(robotId);
        savedJoystick.setButtonCode("BUTTON_A");
        savedJoystick.setActionId(actionId);
        savedJoystick.setStatus(1);
        savedJoystick.setCreatedDate(LocalDateTime.now());

        when(joystickRepository.save(any(Joystick.class))).thenReturn(savedJoystick);

        JoystickDto result = service.create(dto);

        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
        assertEquals(robotId, result.getRobotId());
        verify(joystickRepository).findByAccountIdAndRobotIdAndButtonCodeAndStatus(
                accountId, robotId, "BUTTON_A", 1);
        verify(actionService).getActionById(actionId);
        verify(joystickRepository).save(any(Joystick.class));
    }

    @Test
    void create_conflictOnButton() {
        UUID accountId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();

        JoystickDto dto = new JoystickDto();
        dto.setAccountId(accountId);
        dto.setRobotId(robotId);
        dto.setButtonCode("BUTTON_A");
        dto.setType("TYPE_A");
        dto.setActionId(UUID.randomUUID());

        Joystick existing = new Joystick();
        when(joystickRepository.findByAccountIdAndRobotIdAndButtonCodeAndStatus(
                accountId, robotId, "BUTTON_A", 1))
                .thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
        verify(joystickRepository).findByAccountIdAndRobotIdAndButtonCodeAndStatus(
                accountId, robotId, "BUTTON_A", 1);
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void create_noActionAssigned() {
        JoystickDto dto = new JoystickDto();
        dto.setAccountId(UUID.randomUUID());
        dto.setRobotId(UUID.randomUUID());
        dto.setButtonCode("BUTTON_A");
        dto.setType("TYPE_A");

        when(joystickRepository.findByAccountIdAndRobotIdAndButtonCodeAndStatus(
                any(), any(), any(), anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void create_multipleActionsAssigned() {
        UUID accountId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();

        JoystickDto dto = new JoystickDto();
        dto.setAccountId(accountId);
        dto.setRobotId(robotId);
        dto.setButtonCode("BUTTON_A");
        dto.setType("TYPE_A");
        dto.setActionId(UUID.randomUUID());
        dto.setDanceId(UUID.randomUUID());

        when(joystickRepository.findByAccountIdAndRobotIdAndButtonCodeAndStatus(
                any(), any(), any(), anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void create_invalidActionId() {
        UUID accountId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();

        JoystickDto dto = new JoystickDto();
        dto.setAccountId(accountId);
        dto.setRobotId(robotId);
        dto.setButtonCode("BUTTON_A");
        dto.setType("TYPE_A");
        dto.setActionId(actionId);

        when(joystickRepository.findByAccountIdAndRobotIdAndButtonCodeAndStatus(
                any(), any(), any(), anyInt()))
                .thenReturn(Optional.empty());
        when(actionService.getActionById(actionId))
                .thenThrow(new ResourceNotFoundException("Action not found"));

        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void update_happyPath() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();

        Joystick existing = new Joystick();
        existing.setId(id);
        existing.setAccountId(accountId);
        existing.setRobotId(robotId);
        existing.setButtonCode("BUTTON_A");
        existing.setStatus(1);
        existing.setActionId(actionId);

        JoystickDto dto = new JoystickDto();
        dto.setAccountId(accountId);
        dto.setRobotId(robotId);
        dto.setButtonCode("BUTTON_B");
        dto.setType("TYPE_A");
        dto.setActionId(actionId);

        when(joystickRepository.findById(id)).thenReturn(Optional.of(existing));
        ActionDto actionDto = new ActionDto();
        when(actionService.getActionById(actionId)).thenReturn(actionDto);
        when(joystickRepository.save(any(Joystick.class))).thenReturn(existing);

        JoystickDto result = service.update(id, dto);

        assertNotNull(result);
        verify(joystickRepository).findById(id);
        verify(actionService).getActionById(actionId);
        verify(joystickRepository).save(any(Joystick.class));
    }

    @Test
    void update_notFound() {
        UUID id = UUID.randomUUID();
        JoystickDto dto = new JoystickDto();

        when(joystickRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.update(id, dto));
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void update_deletedJoystick() {
        UUID id = UUID.randomUUID();
        Joystick existing = new Joystick();
        existing.setId(id);
        existing.setStatus(0);

        JoystickDto dto = new JoystickDto();

        when(joystickRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> service.update(id, dto));
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void update_noActionAssigned() {
        UUID id = UUID.randomUUID();
        Joystick existing = new Joystick();
        existing.setId(id);
        existing.setStatus(1);

        JoystickDto dto = new JoystickDto();
        dto.setAccountId(UUID.randomUUID());
        dto.setRobotId(UUID.randomUUID());
        dto.setButtonCode("BUTTON_A");
        dto.setType("TYPE_A");

        when(joystickRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> service.update(id, dto));
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void patch_happyPath() {
        UUID id = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();

        Joystick existing = new Joystick();
        existing.setId(id);
        existing.setStatus(1);
        existing.setActionId(actionId);

        JoystickDto dto = new JoystickDto();
        dto.setButtonCode("BUTTON_B");

        when(joystickRepository.findById(id)).thenReturn(Optional.of(existing));
        when(joystickRepository.save(any(Joystick.class))).thenReturn(existing);

        JoystickDto result = service.patch(id, dto);

        assertNotNull(result);
        verify(joystickRepository).findById(id);
        verify(joystickRepository).save(any(Joystick.class));
    }

    @Test
    void patch_notFound() {
        UUID id = UUID.randomUUID();
        JoystickDto dto = new JoystickDto();

        when(joystickRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.patch(id, dto));
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void patch_deletedJoystick() {
        UUID id = UUID.randomUUID();
        Joystick existing = new Joystick();
        existing.setId(id);
        existing.setStatus(0);

        JoystickDto dto = new JoystickDto();

        when(joystickRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> service.patch(id, dto));
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void patch_noActionAfterUpdate() {
        UUID id = UUID.randomUUID();

        Joystick existing = new Joystick();
        existing.setId(id);
        existing.setStatus(1);

        JoystickDto dto = new JoystickDto();
        dto.setActionId(null);
        dto.setDanceId(null);
        dto.setExpressionId(null);
        dto.setSkillId(null);
        dto.setExtendedActionId(null);

        when(joystickRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> service.patch(id, dto));
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void delete_happyPath() {
        UUID id = UUID.randomUUID();
        Joystick existing = new Joystick();
        existing.setId(id);
        existing.setStatus(1);

        when(joystickRepository.findById(id)).thenReturn(Optional.of(existing));
        when(joystickRepository.save(any(Joystick.class))).thenReturn(existing);

        service.delete(id);

        verify(joystickRepository).findById(id);
        verify(joystickRepository).save(any(Joystick.class));
    }

    @Test
    void delete_notFound() {
        UUID id = UUID.randomUUID();
        when(joystickRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.delete(id));
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void delete_alreadyDeleted() {
        UUID id = UUID.randomUUID();
        Joystick existing = new Joystick();
        existing.setId(id);
        existing.setStatus(0);

        when(joystickRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> service.delete(id));
        verify(joystickRepository, never()).save(any());
    }

    @Test
    void create_withDance() {
        UUID accountId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();
        UUID danceId = UUID.randomUUID();

        JoystickDto dto = new JoystickDto();
        dto.setAccountId(accountId);
        dto.setRobotId(robotId);
        dto.setButtonCode("BUTTON_A");
        dto.setType("TYPE_A");
        dto.setDanceId(danceId);

        when(joystickRepository.findByAccountIdAndRobotIdAndButtonCodeAndStatus(
                any(), any(), any(), anyInt()))
                .thenReturn(Optional.empty());
        DanceDto danceDto = new DanceDto();
        when(danceService.getById(danceId)).thenReturn(danceDto);

        Joystick savedJoystick = new Joystick();
        savedJoystick.setId(UUID.randomUUID());
        savedJoystick.setStatus(1);
        savedJoystick.setCreatedDate(LocalDateTime.now());

        when(joystickRepository.save(any(Joystick.class))).thenReturn(savedJoystick);

        JoystickDto result = service.create(dto);

        assertNotNull(result);
        verify(danceService).getById(danceId);
        verify(joystickRepository).save(any(Joystick.class));
    }

    @Test
    void create_withExpression() {
        UUID accountId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();
        UUID expressionId = UUID.randomUUID();

        JoystickDto dto = new JoystickDto();
        dto.setAccountId(accountId);
        dto.setRobotId(robotId);
        dto.setButtonCode("BUTTON_A");
        dto.setType("TYPE_A");
        dto.setExpressionId(expressionId);

        when(joystickRepository.findByAccountIdAndRobotIdAndButtonCodeAndStatus(
                any(), any(), any(), anyInt()))
                .thenReturn(Optional.empty());
        ExpressionDto expressionDto = new ExpressionDto();
        when(expressionService.getById(expressionId)).thenReturn(expressionDto);

        Joystick savedJoystick = new Joystick();
        savedJoystick.setId(UUID.randomUUID());
        savedJoystick.setStatus(1);
        savedJoystick.setCreatedDate(LocalDateTime.now());

        when(joystickRepository.save(any(Joystick.class))).thenReturn(savedJoystick);

        JoystickDto result = service.create(dto);

        assertNotNull(result);
        verify(expressionService).getById(expressionId);
        verify(joystickRepository).save(any(Joystick.class));
    }

    @Test
    void create_withSkill() {
        UUID accountId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();

        JoystickDto dto = new JoystickDto();
        dto.setAccountId(accountId);
        dto.setRobotId(robotId);
        dto.setButtonCode("BUTTON_A");
        dto.setType("TYPE_A");
        dto.setSkillId(skillId);

        when(joystickRepository.findByAccountIdAndRobotIdAndButtonCodeAndStatus(
                any(), any(), any(), anyInt()))
                .thenReturn(Optional.empty());
        SkillDto skillDto = new SkillDto();
        when(skillService.getSkillById(skillId)).thenReturn(skillDto);

        Joystick savedJoystick = new Joystick();
        savedJoystick.setId(UUID.randomUUID());
        savedJoystick.setStatus(1);
        savedJoystick.setCreatedDate(LocalDateTime.now());

        when(joystickRepository.save(any(Joystick.class))).thenReturn(savedJoystick);

        JoystickDto result = service.create(dto);

        assertNotNull(result);
        verify(skillService).getSkillById(skillId);
        verify(joystickRepository).save(any(Joystick.class));
    }
}
