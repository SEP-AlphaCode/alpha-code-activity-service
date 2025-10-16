package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.ActivityDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.entity.Activity;
import com.alpha_code.alpha_code_activity_service.exception.ConflictException;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.mapper.ActivityMapper;
import com.alpha_code.alpha_code_activity_service.repository.ActivityRepository;
import com.alpha_code.alpha_code_activity_service.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository repository;

    @Override
    @Cacheable(value = "activities_list", key = "{#page, #size, #keyword, #accountId, #modelId, #status}")
    public PagedResult<ActivityDto> getAll(int page, int size, String keyword, UUID accountId, UUID modelId, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Activity> pageResult;

        pageResult = repository.searchActivities(keyword, accountId, modelId, status, pageable);

        return new PagedResult<>(pageResult.map(ActivityMapper::toDto));
    }

    @Override
    @Cacheable(value = "activities", key = "#id")
    public ActivityDto getActivityById(UUID id) {
        var activity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));
        return ActivityMapper.toDto(activity);
    }

    @Override
    @Cacheable(value = "activities_list", key = "#accountId")
    public PagedResult<ActivityDto> getByAccountId(UUID accountId, UUID modelId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Activity> pageResult;
        pageResult = repository.findAllByAccountIdAndRobotModelIdAndStatusNot(accountId, modelId, 0, pageable);

        return  new PagedResult<>(pageResult.map(ActivityMapper::toDto));
    }

    @Override
    @Cacheable(value = "activities_list", key = "#type")
    public List<ActivityDto> getByType(String type, UUID modelId) {
        return repository.findAllByTypeIgnoreCaseAndRobotModelIdAndStatusNot(type, modelId, 0)
                .stream().map(ActivityMapper::toDto).toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "activities_list", allEntries = true)
    public ActivityDto createActivity(ActivityDto dto) {

        var existed = repository.findByNameIgnoreCaseAndRobotModelIdAndStatusNot(dto.getName(), dto.getRobotModelId(),0);

        if(existed.isPresent()){
            throw new ConflictException("Activity already exists with name: " + dto.getName());
        }
        
        var activity = ActivityMapper.toEntity(dto);
        activity.setCreatedDate(LocalDateTime.now());

        Activity savedActivity = repository.save(activity);
        return ActivityMapper.toDto(savedActivity);
    }

    @Override
    @Transactional
    @CacheEvict(value = "activities_list", allEntries = true)
    @CachePut(value = "activities", key = "#id")
    public ActivityDto updateActivity(UUID id, ActivityDto dto) {
        var activity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        var existed = repository.findByNameIgnoreCaseAndRobotModelIdAndStatusNot(dto.getName(), dto.getRobotModelId(),0);
        if (existed.isPresent() && existed.get().getId() != activity.getId()) {
            throw new RuntimeException("Activity already exists with name: " + dto.getName());
        }

        activity.setName(dto.getName());
        activity.setData(dto.getData());
        activity.setType(dto.getType());
        activity.setStatus(dto.getStatus());
        activity.setAccountId(dto.getAccountId());
        activity.setRobotModelId(dto.getRobotModelId());
        activity.setLastUpdated(LocalDateTime.now());

        Activity savedActivity = repository.save(activity);
        return ActivityMapper.toDto(savedActivity);
    }

    @Override
    @Transactional
    @CacheEvict(value = "activities_list", allEntries = true)
    @CachePut(value = "activities", key = "#id")
    public ActivityDto patchUpdateActivity(UUID id, ActivityDto dto) {
        var activity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        var existed = repository.findByNameIgnoreCaseAndRobotModelIdAndStatusNot(dto.getName(), dto.getRobotModelId(),0);
        if (existed.isPresent() && existed.get().getId() != activity.getId()) {
            throw new RuntimeException("Activity already exists with name: " + dto.getName());
        }

        if (dto.getName() != null) {
            activity.setName(dto.getName());
        }
        if (dto.getData() != null) {
            activity.setData(dto.getData());
        }
        if (dto.getType() != null) {
            activity.setType(dto.getType());
        }
        if (dto.getStatus() != null) {
            activity.setStatus(dto.getStatus());
        }
        if (dto.getAccountId() != null) {
            activity.setAccountId(dto.getAccountId());
        }

        if(dto.getRobotModelId() != null) {
            activity.setRobotModelId(dto.getRobotModelId());
        }

        activity.setLastUpdated(LocalDateTime.now());

        Activity savedActivity = repository.save(activity);
        return ActivityMapper.toDto(savedActivity);
    }

    @Override
    @Transactional
    @CacheEvict(value = "activities_list", allEntries = true)
    @CachePut(value = "activities", key = "#id")
    public ActivityDto changeActivityStatus(UUID id, Integer status) {
        var activity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        activity.setStatus(status);
        activity.setLastUpdated(LocalDateTime.now());

        Activity savedActivity = repository.save(activity);
        return ActivityMapper.toDto(savedActivity);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"activities_list", "activities"}, allEntries = true)
    public String deleteActivity(UUID id) {
        var activity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        activity.setStatus(0);
        activity.setLastUpdated(LocalDateTime.now());

        repository.save(activity);

        return "Activity deleted successfully!";
    }
}
