package com.alpha_code.alpha_code_activity_service.controller;

import com.alpha_code.alpha_code_activity_service.dto.ActionDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.service.ActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/actions")
@RequiredArgsConstructor
@Tag(name = "Actions")
@Validated
public class ActionController {

    private final ActionService service;

    @GetMapping
    @Operation(summary = "Get all actions with pagination and optional filters")
    public PagedResult<ActionDto> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int size,
                                         @RequestParam(value = "robotModelId", required = false) UUID robotModelId,
                                         @RequestParam(value = "name", required = false) String name,
                                         @RequestParam(value = "code", required = false) String code,
                                         @RequestParam(value = "status", required = false) Integer status,
                                         @RequestParam(value = "canInterrupt", required = false) Boolean canInterrupt,
                                         @RequestParam(value = "duration", required = false) Integer duration) {
        return service.searchActions(page, size, robotModelId, name, code, status, canInterrupt, duration);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get action by id")
    public ActionDto getOne(@PathVariable UUID id) {
        return service.getActionById(id);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get action by name")
    public ActionDto getByName(@PathVariable String name) {
        return service.getActionByName(name);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get action by code")
    public ActionDto getByCode(@PathVariable String code) {
        return service.getActionByCode(code);
    }

    @GetMapping("/robot-model")
    @Operation(summary = "Get action by robot model id")
    public PagedResult<ActionDto> getByRobotModelId(@RequestParam UUID robotModelId, @RequestParam(value = "page", defaultValue = "1") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        return service.getActionByRobotModelId(robotModelId, page, size);
    }

    @PostMapping
    @Operation(summary = "Create new action")
    public ActionDto create(@RequestBody ActionDto actionDto) {
        return service.createAction(actionDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update action by id")
    public ActionDto update(@PathVariable UUID id, @RequestBody ActionDto actionDto) {
        return service.updateAction(id, actionDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch update action by id")
    public ActionDto patchUpdate(@PathVariable UUID id, @RequestBody ActionDto actionDto) {
        return service.patchUpdateAction(id, actionDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete action by id")
    public String delete(@PathVariable UUID id) {
        return service.deleteAction(id);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Change action status")
    public ActionDto changeStatus(@PathVariable UUID id, @RequestBody Integer status) {
        return service.changeActionStatus(id, status);
    }
}
