package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.dto.SkillDto;
import com.alpha_code.alpha_code_activity_service.entity.Skill;
import com.alpha_code.alpha_code_activity_service.exception.ConflictException;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.grpc.client.RobotServiceClient;
import com.alpha_code.alpha_code_activity_service.repository.SkillRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {

    @Mock
    SkillRepository skillRepository;

    @Mock
    RobotServiceClient robotServiceClient;

    @InjectMocks
    SkillServiceImpl service;

    @Test
    void searchSkills_happyPath() {
        UUID robotModelId = UUID.randomUUID();
        Skill skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setName("Test Skill");
        skill.setCode("TEST_SKILL");
        skill.setRobotModelId(robotModelId);
        skill.setStatus(1);
        skill.setCreatedDate(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Skill> skillPage = new PageImpl<>(List.of(skill), pageable, 1);

        when(skillRepository.searchSkills(any(), any())).thenReturn(skillPage);
        when(robotServiceClient.getRobotModelsByIds(any())).thenReturn(Collections.emptyMap());

        PagedResult<SkillDto> result = service.searchSkills(1, 10, "test");

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        verify(skillRepository).searchSkills(any(), any());
    }

    @Test
    void searchSkills_emptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Skill> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(skillRepository.searchSkills(any(), any())).thenReturn(emptyPage);

        PagedResult<SkillDto> result = service.searchSkills(1, 10, null);

        assertNotNull(result);
        assertEquals(0, result.getTotalCount());
    }

    @Test
    void searchSkills_withRobotModelName() {
        UUID robotModelId = UUID.randomUUID();
        Skill skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setName("Test Skill");
        skill.setCode("TEST_SKILL");
        skill.setRobotModelId(robotModelId);
        skill.setStatus(1);
        skill.setCreatedDate(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Skill> skillPage = new PageImpl<>(List.of(skill), pageable, 1);

        Robot.RobotModelInformation modelInfo = Robot.RobotModelInformation.newBuilder()
                .setId(robotModelId.toString())
                .setName("Test Robot Model")
                .build();
        Map<String, Robot.RobotModelInformation> modelMap = Map.of(robotModelId.toString(), modelInfo);

        when(skillRepository.searchSkills(any(), any())).thenReturn(skillPage);
        when(robotServiceClient.getRobotModelsByIds(any())).thenReturn(modelMap);

        PagedResult<SkillDto> result = service.searchSkills(1, 10, "test");

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
    }

    @Test
    void getSkillById_happyPath() {
        UUID id = UUID.randomUUID();
        Skill skill = new Skill();
        skill.setId(id);
        skill.setName("Test Skill");
        skill.setCode("TEST_SKILL");
        skill.setStatus(1);
        skill.setCreatedDate(LocalDateTime.now());

        when(skillRepository.findById(id)).thenReturn(Optional.of(skill));

        SkillDto result = service.getSkillById(id);

        assertNotNull(result);
        assertEquals("Test Skill", result.getName());
        assertEquals("TEST_SKILL", result.getCode());
        verify(skillRepository).findById(id);
    }

    @Test
    void getSkillById_notFound() {
        UUID id = UUID.randomUUID();
        when(skillRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getSkillById(id));
        verify(skillRepository).findById(id);
    }

    @Test
    void getSkillByName_happyPath() {
        String name = "Test Skill";
        Skill skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setName(name);
        skill.setCode("TEST_SKILL");
        skill.setStatus(1);

        when(skillRepository.findByNameIgnoreCaseAndStatusNot(name, 0)).thenReturn(Optional.of(skill));

        SkillDto result = service.getSkillByName(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(skillRepository).findByNameIgnoreCaseAndStatusNot(name, 0);
    }

    @Test
    void getSkillByName_notFound() {
        String name = "Non-existent Skill";
        when(skillRepository.findByNameIgnoreCaseAndStatusNot(name, 0)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getSkillByName(name));
    }

    @Test
    void getSkillByCode_happyPath() {
        String code = "TEST_SKILL";
        Skill skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setName("Test Skill");
        skill.setCode(code);
        skill.setStatus(1);

        when(skillRepository.findByCodeIgnoreCaseAndStatusNot(code, 0)).thenReturn(Optional.of(skill));

        SkillDto result = service.getSkillByCode(code);

        assertNotNull(result);
        assertEquals(code, result.getCode());
        verify(skillRepository).findByCodeIgnoreCaseAndStatusNot(code, 0);
    }

    @Test
    void getSkillByCode_notFound() {
        String code = "NON_EXISTENT";
        when(skillRepository.findByCodeIgnoreCaseAndStatusNot(code, 0)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getSkillByCode(code));
    }

    @Test
    void getSkillByRobotModelId_happyPath() {
        UUID robotModelId = UUID.randomUUID();
        Skill skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setName("Test Skill");
        skill.setRobotModelId(robotModelId);
        skill.setStatus(1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Skill> skillPage = new PageImpl<>(List.of(skill), pageable, 1);

        when(skillRepository.findByRobotModelIdAndStatusNot(robotModelId, 0, pageable))
                .thenReturn(skillPage);

        PagedResult<SkillDto> result = service.getSkillByRobotModelId(robotModelId, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        verify(skillRepository).findByRobotModelIdAndStatusNot(robotModelId, 0, pageable);
    }

    @Test
    void createSkill_happyPath() {
        SkillDto skillDto = new SkillDto();
        skillDto.setName("New Skill");
        skillDto.setCode("NEW_SKILL");
        skillDto.setStatus(1);
        skillDto.setIcon("icon.png");
        skillDto.setRobotModelId(UUID.randomUUID());

        when(skillRepository.findByCodeIgnoreCaseAndStatusNot("NEW_SKILL", 0))
                .thenReturn(Optional.empty());
        when(skillRepository.findByNameIgnoreCaseAndStatusNot("New Skill", 0))
                .thenReturn(Optional.empty());

        Skill savedSkill = new Skill();
        savedSkill.setId(UUID.randomUUID());
        savedSkill.setName("New Skill");
        savedSkill.setCode("NEW_SKILL");
        savedSkill.setStatus(1);
        savedSkill.setCreatedDate(LocalDateTime.now());

        when(skillRepository.save(any(Skill.class))).thenReturn(savedSkill);

        SkillDto result = service.createSkill(skillDto);

        assertNotNull(result);
        assertEquals("New Skill", result.getName());
        verify(skillRepository).findByCodeIgnoreCaseAndStatusNot("NEW_SKILL", 0);
        verify(skillRepository).findByNameIgnoreCaseAndStatusNot("New Skill", 0);
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void createSkill_conflictOnCode() {
        SkillDto skillDto = new SkillDto();
        skillDto.setName("New Skill");
        skillDto.setCode("EXISTING_CODE");

        Skill existingSkill = new Skill();
        when(skillRepository.findByCodeIgnoreCaseAndStatusNot("EXISTING_CODE", 0))
                .thenReturn(Optional.of(existingSkill));

        assertThrows(ConflictException.class, () -> service.createSkill(skillDto));
        verify(skillRepository).findByCodeIgnoreCaseAndStatusNot("EXISTING_CODE", 0);
        verify(skillRepository, never()).save(any());
    }

    @Test
    void createSkill_conflictOnName() {
        SkillDto skillDto = new SkillDto();
        skillDto.setName("Existing Name");
        skillDto.setCode("NEW_CODE");

        when(skillRepository.findByCodeIgnoreCaseAndStatusNot("NEW_CODE", 0))
                .thenReturn(Optional.empty());
        when(skillRepository.findByNameIgnoreCaseAndStatusNot("Existing Name", 0))
                .thenReturn(Optional.of(new Skill()));

        assertThrows(ResourceNotFoundException.class, () -> service.createSkill(skillDto));
        verify(skillRepository).findByCodeIgnoreCaseAndStatusNot("NEW_CODE", 0);
        verify(skillRepository).findByNameIgnoreCaseAndStatusNot("Existing Name", 0);
        verify(skillRepository, never()).save(any());
    }

    @Test
    void updateSkill_happyPath() {
        UUID id = UUID.randomUUID();
        Skill existingSkill = new Skill();
        existingSkill.setId(id);
        existingSkill.setName("Old Name");
        existingSkill.setCode("OLD_CODE");
        existingSkill.setStatus(1);

        SkillDto skillDto = new SkillDto();
        skillDto.setName("New Name");
        skillDto.setCode("NEW_CODE");
        skillDto.setStatus(2);
        skillDto.setIcon("new_icon.png");
        skillDto.setRobotModelId(UUID.randomUUID());

        when(skillRepository.findById(id)).thenReturn(Optional.of(existingSkill));
        when(skillRepository.save(any(Skill.class))).thenReturn(existingSkill);

        SkillDto result = service.updateSkill(id, skillDto);

        assertNotNull(result);
        verify(skillRepository).findById(id);
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void updateSkill_notFound() {
        UUID id = UUID.randomUUID();
        SkillDto skillDto = new SkillDto();

        when(skillRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateSkill(id, skillDto));
        verify(skillRepository).findById(id);
        verify(skillRepository, never()).save(any());
    }

    @Test
    void patchUpdateSkill_happyPath() {
        UUID id = UUID.randomUUID();
        Skill existingSkill = new Skill();
        existingSkill.setId(id);
        existingSkill.setName("Old Name");
        existingSkill.setCode("OLD_CODE");
        existingSkill.setStatus(1);

        SkillDto skillDto = new SkillDto();
        skillDto.setName("New Name");

        when(skillRepository.findById(id)).thenReturn(Optional.of(existingSkill));
        when(skillRepository.save(any(Skill.class))).thenReturn(existingSkill);

        SkillDto result = service.patchUpdateSkill(id, skillDto);

        assertNotNull(result);
        verify(skillRepository).findById(id);
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void patchUpdateSkill_partialUpdate() {
        UUID id = UUID.randomUUID();
        Skill existingSkill = new Skill();
        existingSkill.setId(id);
        existingSkill.setName("Old Name");
        existingSkill.setCode("OLD_CODE");
        existingSkill.setStatus(1);
        existingSkill.setIcon("old_icon.png");

        SkillDto skillDto = new SkillDto();
        skillDto.setIcon("new_icon.png");

        when(skillRepository.findById(id)).thenReturn(Optional.of(existingSkill));
        when(skillRepository.save(any(Skill.class))).thenReturn(existingSkill);

        SkillDto result = service.patchUpdateSkill(id, skillDto);

        assertNotNull(result);
        verify(skillRepository).findById(id);
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void patchUpdateSkill_notFound() {
        UUID id = UUID.randomUUID();
        SkillDto skillDto = new SkillDto();

        when(skillRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.patchUpdateSkill(id, skillDto));
        verify(skillRepository, never()).save(any());
    }

    @Test
    void changeSkillStatus_happyPath() {
        UUID id = UUID.randomUUID();
        Skill existingSkill = new Skill();
        existingSkill.setId(id);
        existingSkill.setStatus(1);

        when(skillRepository.findById(id)).thenReturn(Optional.of(existingSkill));
        when(skillRepository.save(any(Skill.class))).thenReturn(existingSkill);

        SkillDto result = service.changeSkillStatus(id, 2);

        assertNotNull(result);
        verify(skillRepository).findById(id);
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void changeSkillStatus_notFound() {
        UUID id = UUID.randomUUID();
        when(skillRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.changeSkillStatus(id, 2));
        verify(skillRepository, never()).save(any());
    }

    @Test
    void deleteSkill_happyPath() {
        UUID id = UUID.randomUUID();
        Skill existingSkill = new Skill();
        existingSkill.setId(id);
        existingSkill.setStatus(1);

        when(skillRepository.findById(id)).thenReturn(Optional.of(existingSkill));
        when(skillRepository.save(any(Skill.class))).thenReturn(existingSkill);

        String result = service.deleteSkill(id);

        assertNotNull(result);
        assertTrue(result.contains("deleted successfully"));
        verify(skillRepository).findById(id);
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void deleteSkill_notFound() {
        UUID id = UUID.randomUUID();
        when(skillRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteSkill(id));
        verify(skillRepository, never()).save(any());
    }

    @Test
    void searchSkills_grpcException() {
        UUID robotModelId = UUID.randomUUID();
        Skill skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setName("Test Skill");
        skill.setRobotModelId(robotModelId);
        skill.setStatus(1);
        skill.setCreatedDate(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Skill> skillPage = new PageImpl<>(List.of(skill), pageable, 1);

        when(skillRepository.searchSkills(any(), any())).thenReturn(skillPage);
        when(robotServiceClient.getRobotModelsByIds(any()))
                .thenThrow(new RuntimeException("gRPC error"));

        PagedResult<SkillDto> result = service.searchSkills(1, 10, "test");

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
    }

    @Test
    void searchSkills_negativePage() {
        UUID robotModelId = UUID.randomUUID();
        Skill skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setName("Test Skill");
        skill.setRobotModelId(robotModelId);
        skill.setStatus(1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Skill> skillPage = new PageImpl<>(List.of(skill), pageable, 1);

        when(skillRepository.searchSkills(any(), any())).thenReturn(skillPage);
        when(robotServiceClient.getRobotModelsByIds(any())).thenReturn(Collections.emptyMap());

        PagedResult<SkillDto> result = service.searchSkills(0, 10, "test");

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
    }
}
