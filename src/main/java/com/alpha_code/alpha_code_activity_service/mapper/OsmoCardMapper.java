package com.alpha_code.alpha_code_activity_service.mapper;


import com.alpha_code.alpha_code_activity_service.dto.OsmoCardDto;
import com.alpha_code.alpha_code_activity_service.entity.OsmoCard;

public class OsmoCardMapper {
    public static OsmoCardDto toDto(OsmoCard osmoCard) {
        if (osmoCard == null) {
            return null;
        }
        OsmoCardDto dto = new OsmoCardDto();
        dto.setId(osmoCard.getId());
        dto.setColor(osmoCard.getColor());
        dto.setName(osmoCard.getName());
        dto.setStatus(osmoCard.getStatus());
        dto.setLastUpdated(osmoCard.getLastUpdated());
        dto.setCreatedDate(osmoCard.getCreatedDate());
        dto.setExpressionId(osmoCard.getExpressionId());
        if (osmoCard.getExpression() != null) {
            dto.setExpressionName(osmoCard.getExpression().getName());
            dto.setExpressionCode(osmoCard.getExpression().getCode());
        }
        dto.setActionId(osmoCard.getActionId());
        if (osmoCard.getAction() != null) {
            dto.setActionName(osmoCard.getAction().getName());
            dto.setActionCode(osmoCard.getAction().getCode());
        }
        dto.setDanceId(osmoCard.getDanceId());
        if (osmoCard.getDance() != null) {
            dto.setDanceName(osmoCard.getDance().getName());
            dto.setDanceCode(osmoCard.getDance().getCode());
        }
        dto.setSkillId(osmoCard.getSkillId());
        if (osmoCard.getSkill() != null) {
            dto.setSkillName(osmoCard.getSkill().getName());
            dto.setSkillCode(osmoCard.getSkill().getCode());
        }

        dto.setExtendedActionId(osmoCard.getExtendedActionId());
        if (osmoCard.getExtendedAction() != null) {
            dto.setExtendedActionName(osmoCard.getExtendedAction().getName());
            dto.setExtendedActionCode(osmoCard.getExtendedAction().getCode());
        }
        return dto;
    } 

    public static OsmoCard toEntity(OsmoCardDto osmoCardDto) {
        if (osmoCardDto == null) {
            return null;
        }
        OsmoCard osmoCard = new OsmoCard();
        osmoCard.setId(osmoCardDto.getId());
        osmoCard.setColor(osmoCardDto.getColor());
        osmoCard.setName(osmoCardDto.getName());
        osmoCard.setStatus(osmoCardDto.getStatus());
        osmoCard.setLastUpdated(osmoCardDto.getLastUpdated());
        osmoCard.setCreatedDate(osmoCardDto.getCreatedDate());
        osmoCard.setExpressionId(osmoCardDto.getExpressionId());
        osmoCard.setActionId(osmoCardDto.getActionId());
        osmoCard.setDanceId(osmoCardDto.getDanceId());
        osmoCard.setSkillId(osmoCardDto.getSkillId());
        osmoCard.setExtendedActionId(osmoCardDto.getExtendedActionId());
        return osmoCard;
    }
}
