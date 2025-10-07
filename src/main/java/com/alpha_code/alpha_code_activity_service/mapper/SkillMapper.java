package com.alpha_code.alpha_code_activity_service.mapper;

import com.alpha_code.alpha_code_activity_service.dto.SkillDto;
import com.alpha_code.alpha_code_activity_service.entity.Skill;

public class SkillMapper {
    public static SkillDto toDto(Skill skill) {
        if (skill == null) {
            return null;
        }
        SkillDto skillDto = new SkillDto();

        skillDto.setId(skill.getId());
        skillDto.setName(skill.getName());
        skillDto.setIcon(skill.getIcon());
        skillDto.setCode(skill.getCode());
        skillDto.setStatus(skill.getStatus());
        skillDto.setLastUpdated(skill.getLastUpdated());
        skillDto.setCreatedDate(skill.getCreatedDate());
        skillDto.setRobotModelId(skill.getRobotModelId());

        return skillDto;
    }

    public static Skill toEntity(SkillDto skillDto) {
        if (skillDto == null) {
            return null;
        }
        Skill skill = new Skill();

        skill.setId(skillDto.getId());
        skill.setName(skillDto.getName());
        skill.setIcon(skillDto.getIcon());
        skill.setCode(skillDto.getCode());
        skill.setStatus(skillDto.getStatus());
        skill.setLastUpdated(skillDto.getLastUpdated());
        skill.setCreatedDate(skillDto.getCreatedDate());
        skill.setRobotModelId(skillDto.getRobotModelId());

        return skill;
    }
}
