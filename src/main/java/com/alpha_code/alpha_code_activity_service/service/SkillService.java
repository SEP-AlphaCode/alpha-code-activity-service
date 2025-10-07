package com.alpha_code.alpha_code_activity_service.service;

import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.dto.SkillDto;

import java.util.UUID;

public interface SkillService {
    PagedResult<SkillDto> searchSkills(int page, int size, String searchTerm);
    SkillDto getSkillById(UUID id);
    SkillDto getSkillByName(String name);
    SkillDto getSkillByCode(String code);
    PagedResult<SkillDto> getSkillByRobotModelId(UUID robotModelId, int page, int size);
    SkillDto createSkill(SkillDto skillDto);
    SkillDto updateSkill(UUID id, SkillDto skillDto);
    SkillDto patchUpdateSkill(UUID id, SkillDto skillDto);
    SkillDto changeSkillStatus(UUID id, Integer status);
    String deleteSkill(UUID id);
}
