package com.alpha_code.alpha_code_activity_service.mapper;

import com.alpha_code.alpha_code_activity_service.dto.ExtendedActionDto;
import com.alpha_code.alpha_code_activity_service.entity.ExtendedAction;

public class ExtendedActionMapper {
    public static ExtendedActionDto toDto(ExtendedAction extendedAction){
        if(extendedAction == null) {
            return null;
        }

        ExtendedActionDto extendedActionDto = new ExtendedActionDto();
        extendedActionDto.setId(extendedAction.getId());
        extendedActionDto.setName(extendedAction.getName());
        extendedActionDto.setIcon(extendedAction.getIcon());
        extendedActionDto.setCode(extendedAction.getCode());
        extendedActionDto.setStatus(extendedAction.getStatus());
        extendedActionDto.setLastUpdated(extendedAction.getLastUpdated());
        extendedActionDto.setCreatedDate(extendedAction.getCreatedDate());
        extendedActionDto.setRobotModelId(extendedAction.getRobotModelId());

        return extendedActionDto;
    }

    public static ExtendedAction toEntity(ExtendedActionDto extendedActionDto){
        if(extendedActionDto == null) {
            return null;
        }

        ExtendedAction extendedAction = new ExtendedAction();
        extendedAction.setId(extendedActionDto.getId());
        extendedAction.setCode(extendedActionDto.getCode());
        extendedAction.setName(extendedActionDto.getName());
        extendedAction.setIcon(extendedActionDto.getIcon());
        extendedAction.setStatus(extendedActionDto.getStatus());
        extendedAction.setLastUpdated(extendedActionDto.getLastUpdated());
        extendedAction.setCreatedDate(extendedActionDto.getCreatedDate());
        extendedAction.setRobotModelId(extendedActionDto.getRobotModelId());
        return extendedAction;
    }
}
