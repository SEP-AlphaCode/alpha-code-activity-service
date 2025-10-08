package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.JoystickDto;
import com.alpha_code.alpha_code_activity_service.entity.Joystick;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.mapper.JoystickMapper;
import com.alpha_code.alpha_code_activity_service.repository.JoystickRepository;
import com.alpha_code.alpha_code_activity_service.service.JoystickService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JoystickServiceImpl implements JoystickService {

    private final JoystickRepository joystickRepository;

    // -----------------------------
    // GET BY ACCOUNT + ROBOT
    // -----------------------------
    @Override
    @Cacheable(value = "joystick_by_robot", key = "{#accountId, #robotId}")
    public List<JoystickDto> getByAccountIdAndRobotId(UUID accountId, UUID robotId) {
        List<Joystick> joysticks = joystickRepository
                .findListByAccountIdAndRobotIdAndStatus(accountId, robotId, 1);

        if (joysticks.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy joystick cho accountId: " + accountId + " và robotId: " + robotId);
        }

        return joysticks.stream()
                .map(JoystickMapper::toDto)
                .collect(Collectors.toList());
    }

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    @Transactional
    @CacheEvict(value = "joystick_by_robot", key = "{#joystickDto.accountId, #joystickDto.robotId}")
    public JoystickDto create(JoystickDto joystickDto) {
        var entity = JoystickMapper.toEntity(joystickDto);

        // Kiểm tra trùng joystick theo account, robot, button
        var existing = joystickRepository.findByAccountIdAndRobotIdAndButtonCodeAndStatus(
                entity.getAccountId(),
                entity.getRobotId(),
                entity.getButtonCode(),
                1
        );

        if (existing.isPresent()) {
            throw new IllegalArgumentException("Nút joystick này đã được gán cho robot, vui lòng chọn nút khác");
        }

        // Validate chỉ chọn 1 loại hành động
        int count = 0;
        if (entity.getActionId() != null) count++;
        if (entity.getDanceId() != null) count++;
        if (entity.getExpressionId() != null) count++;
        if (entity.getSkillId() != null) count++;
        if (entity.getExtendedActionId() != null) count++;

        if (count == 0) {
            throw new IllegalArgumentException("Joystick phải được gán ít nhất một loại hành động (Action, Dance, Expression, Skill hoặc Extended Action)");
        }
        if (count > 1) {
            throw new IllegalArgumentException("Joystick chỉ được phép gán một loại hành động duy nhất");
        }

        entity.setStatus(1);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setLastUpdated(null);

        var saved = joystickRepository.save(entity);
        return JoystickMapper.toDto(saved);
    }

    // -----------------------------
    // UPDATE (PUT)
    // -----------------------------
    @Override
    @Transactional
    @CacheEvict(value = "joystick_by_robot", key = "{#joystickDto.accountId, #joystickDto.robotId}")
    public JoystickDto update(UUID id, JoystickDto joystickDto) {
        var existing = joystickRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy joystick với ID: " + id));

        if (existing.getStatus() == 0) {
            throw new IllegalArgumentException("Joystick này đã bị xóa và không thể cập nhật");
        }

        // Validate chỉ 1 loại hành động
        int count = 0;
        if (joystickDto.getActionId() != null) count++;
        if (joystickDto.getDanceId() != null) count++;
        if (joystickDto.getExpressionId() != null) count++;
        if (joystickDto.getSkillId() != null) count++;
        if (joystickDto.getExtendedActionId() != null) count++;

        if (count == 0) {
            throw new IllegalArgumentException("Joystick phải được gán ít nhất một loại hành động (Action, Dance, Expression, Skill hoặc Extended Action)");
        }
        if (count > 1) {
            throw new IllegalArgumentException("Joystick chỉ được phép gán một loại hành động duy nhất");
        }

        existing.setButtonCode(joystickDto.getButtonCode());
        existing.setActionId(joystickDto.getActionId());
        existing.setDanceId(joystickDto.getDanceId());
        existing.setExpressionId(joystickDto.getExpressionId());
        existing.setSkillId(joystickDto.getSkillId());
        existing.setExtendedActionId(joystickDto.getExtendedActionId());
        existing.setLastUpdated(LocalDateTime.now());

        var updated = joystickRepository.save(existing);
        return JoystickMapper.toDto(updated);
    }

    // -----------------------------
    // PATCH
    // -----------------------------
    @Override
    @Transactional
    @CacheEvict(value = "joystick_by_robot", key = "{#joystickDto.accountId, #joystickDto.robotId}")
    public JoystickDto patch(UUID id, JoystickDto joystickDto) {
        var existing = joystickRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy joystick với ID: " + id));

        if (existing.getStatus() == 0) {
            throw new IllegalArgumentException("Joystick này đã bị xóa và không thể cập nhật");
        }

        if (joystickDto.getButtonCode() != null) existing.setButtonCode(joystickDto.getButtonCode());
        if (joystickDto.getActionId() != null) existing.setActionId(joystickDto.getActionId());
        if (joystickDto.getDanceId() != null) existing.setDanceId(joystickDto.getDanceId());
        if (joystickDto.getExpressionId() != null) existing.setExpressionId(joystickDto.getExpressionId());
        if (joystickDto.getSkillId() != null) existing.setSkillId(joystickDto.getSkillId());
        if (joystickDto.getExtendedActionId() != null) existing.setExtendedActionId(joystickDto.getExtendedActionId());

        existing.setLastUpdated(LocalDateTime.now());

        // Validate chỉ 1 loại hành động
        int count = 0;
        if (existing.getActionId() != null) count++;
        if (existing.getDanceId() != null) count++;
        if (existing.getExpressionId() != null) count++;
        if (existing.getSkillId() != null) count++;
        if (existing.getExtendedActionId() != null) count++;

        if (count == 0) {
            throw new IllegalArgumentException("Joystick phải được gán ít nhất một loại hành động");
        }
        if (count > 1) {
            throw new IllegalArgumentException("Joystick chỉ được phép gán một loại hành động duy nhất");
        }

        var updated = joystickRepository.save(existing);
        return JoystickMapper.toDto(updated);
    }

    // -----------------------------
    // DELETE (SOFT DELETE)
    // -----------------------------
    @Override
    @Transactional
    @CacheEvict(value = "joystick_by_robot", allEntries = true)
    public void delete(UUID id) {
        var existing = joystickRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy joystick với ID: " + id));

        if (existing.getStatus() == 0) {
            throw new IllegalArgumentException("Joystick này đã bị xóa trước đó");
        }

        existing.setStatus(0);
        existing.setLastUpdated(LocalDateTime.now());
        joystickRepository.save(existing);
    }
}
