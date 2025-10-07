package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.ActionDto;
import com.alpha_code.alpha_code_activity_service.dto.ExtendedActionDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.entity.Action;
import com.alpha_code.alpha_code_activity_service.exception.ConflictException;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.mapper.ActionMapper;
import com.alpha_code.alpha_code_activity_service.mapper.ExtendedActionMapper;
import com.alpha_code.alpha_code_activity_service.repository.ActionRepository;
import com.alpha_code.alpha_code_activity_service.service.ActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {

    private final ActionRepository actionRepository;

    @Override
    @Cacheable(value = "actions_list", key = "{#page, #size, #robotModelId ,#name, #code, #status, #canInterrupt, #duration}")
    public PagedResult<ActionDto> searchActions(int page, int size, UUID robotModelId, String name, String code, Integer status, Boolean canInterrupt, Integer duration) {
        Pageable pageable = PageRequest.of(page - 1,size);
        Page<Action> actions = actionRepository.searchActions(robotModelId, name, code, status, canInterrupt, duration, pageable);

        return new PagedResult<>(actions.map(ActionMapper::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "actions", key = "#id")
    public ActionDto getActionById(UUID id) {
        Action action = actionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found with id: " + id));
        return ActionMapper.toDto(action);
    }

    @Override
    @Cacheable(value = "actions", key = "#name")
    public ActionDto getActionByName(String name) {
        Action action = actionRepository.findByNameIgnoreCaseAndStatusNot(name, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found with name: " + name));
        return ActionMapper.toDto(action);
    }

    @Override
    @Cacheable(value = "actions", key = "#code")
    public ActionDto getActionByCode(String code) {
        Action action = actionRepository.findByCodeIgnoreCaseAndStatusNot(code, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found with code: " + code));
        return ActionMapper.toDto(action);
    }

    @Override
    @Cacheable(value = "actions", key = "#robotModelId")
    public PagedResult<ActionDto> getActionByRobotModelId(UUID robotModelId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1,size);
        Page<Action>  actions = actionRepository.findByRobotModelIdAndStatusNot(robotModelId, 0, pageable);
        // Map sang DTO
        Page<ActionDto> dtoPage = actions.map(ActionMapper::toDto);

        // Trả về dạng PagedResult (custom wrapper)
        return new PagedResult<>(dtoPage);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"actions_list"}, allEntries = true)
    public ActionDto createAction(ActionDto actionDto) {

        var existed = actionRepository.findByCodeIgnoreCaseAndStatusNot(actionDto.getCode(), 0);
        if (existed.isPresent()) {
            throw new ConflictException("Đã tồn tại action với code: " + actionDto.getCode());
        }

        existed = actionRepository.findByNameIgnoreCaseAndStatusNot(actionDto.getName(), 0);
        if (existed.isPresent()) {
            throw new ConflictException("Đã tồn tại action với tên: " + actionDto.getName());
        }

        Action action = ActionMapper.toEntity(actionDto);
        action.setCreatedDate(LocalDateTime.now());
        action.setLastUpdated(LocalDateTime.now());
        Action savedAction = actionRepository.save(action);
        return ActionMapper.toDto(savedAction);
    }

    @Override
    @Transactional
    @CachePut(value = "actions", key = "#id")
    @CacheEvict(value = {"actions_list"}, allEntries = true)
    public ActionDto updateAction(UUID id, ActionDto actionDto) {
        Action existingAction = actionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hành động với id: " + id));

        existingAction.setName(actionDto.getName());
        existingAction.setDescription(actionDto.getDescription());
        existingAction.setStatus(actionDto.getStatus());
        existingAction.setDuration(actionDto.getDuration());
        existingAction.setCanInterrupt(actionDto.getCanInterrupt());
        existingAction.setRobotModelId(actionDto.getRobotModelId());
        existingAction.setIcon(actionDto.getIcon());
        existingAction.setLastUpdated(LocalDateTime.now());

        Action updatedAction = actionRepository.save(existingAction);
        return ActionMapper.toDto(updatedAction);
    }

    @Override
    @Transactional
    @CachePut(value = "actions", key = "#id")
    @CacheEvict(value = {"actions_list"}, allEntries = true)
    public ActionDto patchUpdateAction(UUID id, ActionDto actionDto) {
        Action existingAction = actionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hành động với id: " + id));

        if (actionDto.getName() != null) {
            existingAction.setName(actionDto.getName());
        }
        if (actionDto.getDescription() != null) {
            existingAction.setDescription(actionDto.getDescription());
        }
        if (actionDto.getStatus() != null) {
            existingAction.setStatus(actionDto.getStatus());
        }
        if (actionDto.getDuration() != null) {
            existingAction.setDuration(actionDto.getDuration());
        }
        if (actionDto.getCanInterrupt() != null) {
            existingAction.setCanInterrupt(actionDto.getCanInterrupt());
        }
        if (actionDto.getRobotModelId() != null) {
            existingAction.setRobotModelId(actionDto.getRobotModelId());
        }
        if (actionDto.getIcon() != null) {
            existingAction.setIcon(actionDto.getIcon());
        }
        existingAction.setLastUpdated(LocalDateTime.now());

        Action updatedAction = actionRepository.save(existingAction);
        return ActionMapper.toDto(updatedAction);

    }

    @Override
    @Transactional
    @CachePut(value = "actions", key = "#id")
    @CacheEvict(value = {"actions_list"}, allEntries = true)
    public ActionDto changeActionStatus(UUID id, Integer status) {
        Action existingAction = actionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hành động với id: " + id));
        existingAction.setStatus(status);
        existingAction.setLastUpdated(LocalDateTime.now());
        Action updatedAction = actionRepository.save(existingAction);
        return ActionMapper.toDto(updatedAction);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"actions", "actions_list"}, key = "#id", allEntries = true)
    public String deleteAction(UUID id) {
        Action existingAction = actionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hành động với id: " + id));

        existingAction.setStatus(0);
        existingAction.setLastUpdated(LocalDateTime.now());
        actionRepository.save(existingAction);
        return "Hành động đã được xóa thành công";
    }
}
