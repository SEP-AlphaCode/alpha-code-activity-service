package com.alpha_code.alpha_code_activity_service.mapper;

import com.alpha_code.alpha_code_activity_service.dto.DanceDto;
import com.alpha_code.alpha_code_activity_service.entity.Dance;

public class DanceMapper {
    public static DanceDto toDto(Dance dance) {
        if (dance == null) {
            return null;
        }

        DanceDto danceDto = new DanceDto();
        danceDto.setId(dance.getId());
        danceDto.setName(dance.getName());
        danceDto.setDuration(dance.getDuration());
        danceDto.setDescription(dance.getDescription());
        danceDto.setCode(dance.getCode());
        danceDto.setStatus(dance.getStatus());
        danceDto.setCreatedDate(dance.getCreatedDate());
        danceDto.setLastUpdated(dance.getLastUpdated());
        danceDto.setIcon(dance.getIcon());
        danceDto.setRobotModelId(dance.getRobotModelId());
        return danceDto;
    }

    public static Dance toEntity(DanceDto dto) {
        if (dto == null) {
            return null;
        }

        Dance dance = new Dance();
        dance.setId(dto.getId());
        dance.setName(dto.getName());
        dance.setDuration(dto.getDuration());
        dance.setDescription(dto.getDescription());
        dance.setCode(dto.getCode());
        dance.setStatus(dto.getStatus());
        dance.setCreatedDate(dto.getCreatedDate());
        dance.setLastUpdated(dto.getLastUpdated());
        dance.setIcon(dto.getIcon());
        dance.setRobotModelId(dto.getRobotModelId());
        return dance;
    }
}
