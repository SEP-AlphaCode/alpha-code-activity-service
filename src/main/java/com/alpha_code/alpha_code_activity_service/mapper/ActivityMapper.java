package com.alpha_code.alpha_code_activity_service.mapper;

import com.alpha_code.alpha_code_activity_service.dto.ActivityDto;
import com.alpha_code.alpha_code_activity_service.entity.Activity;

public class ActivityMapper {
    public static ActivityDto toDto(Activity activity) {
        if (activity == null) {
            return null;
        }

        ActivityDto activityDto = new ActivityDto();
        activityDto.setId(activity.getId());
        activityDto.setName(activity.getName());
        activityDto.setData(activity.getData());
        activityDto.setType(activity.getType());
        activityDto.setCreatedDate(activity.getCreatedDate());
        activityDto.setLastUpdated(activity.getLastUpdated());
        activityDto.setStatus(activity.getStatus());
        activityDto.setAccountId(activity.getAccountId());
        return activityDto;
    }

    public static Activity toEntity(ActivityDto dto) {
        if (dto == null) {
            return null;
        }

        Activity activity = new Activity();
        activity.setId(dto.getId());
        activity.setName(dto.getName());
        activity.setData(dto.getData());
        activity.setType(dto.getType());
        activity.setCreatedDate(dto.getCreatedDate());
        activity.setLastUpdated(dto.getLastUpdated());
        activity.setStatus(dto.getStatus());
        activity.setAccountId(dto.getAccountId());
        return activity;
    }
}
