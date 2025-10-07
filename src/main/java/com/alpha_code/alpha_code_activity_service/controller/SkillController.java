package com.alpha_code.alpha_code_activity_service.controller;


import com.alpha_code.alpha_code_activity_service.dto.SkillDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Skill management APIs")
@Validated
public class SkillController {
    private final SkillService skillService;

    @GetMapping
    @Operation(summary = "Get all extended actions with pagination and optional filters")
    public PagedResult<SkillDto> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                                 @RequestParam(value = "search", required = false) String search)
    {
        return skillService.searchSkills(page, size, search);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get action by id")
    public SkillDto getOne(@PathVariable UUID id) {
        return skillService.getSkillById(id);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get action by name")
    public SkillDto getByName(@PathVariable String name) {
        return skillService.getSkillByName(name);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get action by code")
    public SkillDto getByCode(@PathVariable String code) {
        return skillService.getSkillByCode(code);
    }

    @GetMapping("/robot-model")
    @Operation(summary = "Get extended action by robot model id")
    public PagedResult<SkillDto> getByRobotModelId(@RequestParam UUID robotModelId, @RequestParam(value = "page", defaultValue = "1") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        return skillService.getSkillByRobotModelId(robotModelId, page, size);
    }

    @PostMapping
    @Operation(summary = "Create new extended action")
    public SkillDto create(@RequestBody SkillDto SkillDto) {
        return skillService.createSkill(SkillDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update extended action by id")
    public SkillDto update(@PathVariable UUID id, @RequestBody SkillDto SkillDto) {
        return skillService.updateSkill(id, SkillDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch update extended action by id")
    public SkillDto patchUpdate(@PathVariable UUID id, @RequestBody SkillDto SkillDto) {
        return skillService.patchUpdateSkill(id, SkillDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete extended action by id")
    public String delete(@PathVariable UUID id) {
        return skillService.deleteSkill(id);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Change extended action status")
    public SkillDto changeStatus(@PathVariable UUID id, @RequestBody Integer status) {
        return skillService.changeSkillStatus(id, status);
    }
}
