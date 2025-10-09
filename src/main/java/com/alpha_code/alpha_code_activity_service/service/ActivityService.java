package com.alpha_code.alpha_code_activity_service.service;

import com.alpha_code.alpha_code_activity_service.dto.ActionDto;
import com.alpha_code.alpha_code_activity_service.dto.ActivityDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;

import java.util.List;
import java.util.UUID;

public interface ActivityService {
    PagedResult<ActivityDto> getAll(int page, int size, String keyword, UUID accountId, Integer status);

    ActivityDto getActivityById(UUID id);

    PagedResult<ActivityDto> getByAccountId(UUID accountId, int page, int size);

    List<ActivityDto> getByType(String type);

    ActivityDto createActivity(ActivityDto dto);

    ActivityDto updateActivity(UUID id, ActivityDto dto);

    ActivityDto patchUpdateActivity(UUID id, ActivityDto dto);

    ActivityDto changeActivityStatus(UUID id, Integer status);

    String deleteActivity(UUID id);
}
