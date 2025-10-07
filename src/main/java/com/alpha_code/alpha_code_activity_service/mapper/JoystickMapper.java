package com.alpha_code.alpha_code_activity_service.mapper;

import com.alpha_code.alpha_code_activity_service.dto.JoystickDto;
import com.alpha_code.alpha_code_activity_service.entity.Joystick;

public class JoystickMapper {
    public static JoystickDto toDto(Joystick joystick) {
        if (joystick == null) {
            return null;
        }
        JoystickDto dto = new JoystickDto();
        dto.setId(joystick.getId());
        dto.setAccountId(joystick.getAccountId());
        dto.setRobotId(joystick.getRobotId());
        dto.setCreatedDate(joystick.getCreatedDate());
        dto.setLastUpdated(joystick.getLastUpdated());
        dto.setButtonCode(joystick.getButtonCode());
        dto.setStatus(joystick.getStatus());
        dto.setType(joystick.getType());

        dto.setSkillId(joystick.getSkillId());
        if(joystick.getSkill()!=null){
            dto.setSkillName(joystick.getSkill().getName());
            dto.setSkillCode(joystick.getSkill().getCode());
        }

        dto.setExtendedActionId(joystick.getExtendedActionId());
        if(joystick.getExtendedAction()!=null){
            dto.setExtendedActionName(joystick.getExtendedAction().getName());
            dto.setExtendedActionCode(joystick.getExtendedAction().getCode());
        }

        dto.setDanceId(joystick.getDanceId());
        if(joystick.getDance()!=null){
            dto.setDanceName(joystick.getDance().getName());
            dto.setDanceCode(joystick.getDance().getCode());
        }

        dto.setActionId(joystick.getActionId());
        if(joystick.getAction()!=null){
            dto.setActionName(joystick.getAction().getName());
            dto.setActionCode(joystick.getAction().getCode());
        }

        dto.setExtendedActionId(joystick.getExtendedActionId());
        if(joystick.getExtendedAction()!=null){
            dto.setExtendedActionName(joystick.getExtendedAction().getName());
            dto.setExtendedActionCode(joystick.getExtendedAction().getCode());
        }

        return dto;
    }

    public static Joystick toEntity(JoystickDto joystickDto) {
        if (joystickDto == null) {
            return null;
        }
        Joystick joystick = new Joystick();
        joystick.setId(joystickDto.getId());
        joystick.setAccountId(joystickDto.getAccountId());
        joystick.setRobotId(joystickDto.getRobotId());
        joystick.setCreatedDate(joystickDto.getCreatedDate());
        joystick.setLastUpdated(joystickDto.getLastUpdated());
        joystick.setButtonCode(joystickDto.getButtonCode());
        joystick.setStatus(joystickDto.getStatus());
        joystick.setType(joystickDto.getType());

        joystick.setSkillId(joystickDto.getSkillId());
        joystick.setExtendedActionId(joystickDto.getExtendedActionId());
        joystick.setDanceId(joystickDto.getDanceId());
        joystick.setActionId(joystickDto.getActionId());

        return joystick;
    }
}
