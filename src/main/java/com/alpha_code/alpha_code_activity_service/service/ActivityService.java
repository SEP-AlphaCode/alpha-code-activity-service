package com.alpha_code.alpha_code_activity_service.service;

import com.alpha_code.alpha_code_activity_service.dto.ActionDto;
import com.alpha_code.alpha_code_activity_service.dto.ActivityDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;

import java.util.List;
import java.util.UUID;

public interface ActivityService {
    PagedResult<ActivityDto> getAll(int page, int size, String keyword, UUID accountId, UUID modelId, Integer status);

    ActivityDto getActivityById(UUID id);

    PagedResult<ActivityDto> getByAccountId(UUID accountId, UUID modelId, int page, int size);

    List<ActivityDto> getByType(String type, UUID modelId);

    ActivityDto createActivity(ActivityDto dto);

    ActivityDto updateActivity(UUID id, ActivityDto dto);

    ActivityDto patchUpdateActivity(UUID id, ActivityDto dto);

    ActivityDto changeActivityStatus(UUID id, Integer status);

    String deleteActivity(UUID id);
}
