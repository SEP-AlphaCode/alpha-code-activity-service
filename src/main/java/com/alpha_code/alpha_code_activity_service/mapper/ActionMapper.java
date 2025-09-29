package com.alpha_code.alpha_code_activity_service.mapper;


import com.alpha_code.alpha_code_activity_service.dto.ActionDto;
import com.alpha_code.alpha_code_activity_service.entity.Action;

public class ActionMapper {

    public static ActionDto toDto(Action action) {
        if (action == null) {
            return null;
        }

        ActionDto actionDto = new ActionDto();
        actionDto.setId(action.getId());
        actionDto.setCreatedDate(action.getCreatedDate());
        actionDto.setLastUpdated(action.getLastUpdated());
        actionDto.setName(action.getName());
        actionDto.setCode(action.getCode());
        actionDto.setDescription(action.getDescription());
        actionDto.setDuration(action.getDuration());
        actionDto.setStatus(action.getStatus());
        actionDto.setCanInterrupt(action.getCanInterrupt());
        actionDto.setRobotModelId(action.getRobotModelId());
        return actionDto;

    }

    public static Action toEntity(ActionDto dto) {
        if (dto == null) {
            return null;
        }

        Action action = new Action();
        action.setId(dto.getId());
        action.setCreatedDate(dto.getCreatedDate());
        action.setLastUpdated(dto.getLastUpdated());
        action.setName(dto.getName());
        action.setCode(dto.getCode());
        action.setDescription(dto.getDescription());
        action.setDuration(dto.getDuration());
        action.setStatus(dto.getStatus());
        action.setCanInterrupt(dto.getCanInterrupt());
        action.setRobotModelId(dto.getRobotModelId());
        return action;
    }


}
