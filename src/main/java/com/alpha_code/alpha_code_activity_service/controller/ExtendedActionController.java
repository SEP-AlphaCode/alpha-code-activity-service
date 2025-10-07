package com.alpha_code.alpha_code_activity_service.controller;

import com.alpha_code.alpha_code_activity_service.dto.ExtendedActionDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.service.ExtendedActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/extended-actions")
@RequiredArgsConstructor
@Tag(name = "Extended Actions", description = "Extended Action management APIs")
@Validated
public class ExtendedActionController {
    private final ExtendedActionService extendedActionService;

    @GetMapping
    @Operation(summary = "Get all extended actions with pagination and optional filters")
    public PagedResult<ExtendedActionDto> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                                 @RequestParam(value = "search", required = false) String search)
                                                {
        return extendedActionService.searchExtendedActions(page, size, search);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get action by id")
    public ExtendedActionDto getOne(@PathVariable UUID id) {
        return extendedActionService.getExtendedActionById(id);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get action by name")
    public ExtendedActionDto getByName(@PathVariable String name) {
        return extendedActionService.getExtendedActionByName(name);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get action by code")
    public ExtendedActionDto getByCode(@PathVariable String code) {
        return extendedActionService.getExtendedActionByCode(code);
    }

    @GetMapping("/robot-model")
    @Operation(summary = "Get extended action by robot model id")
    public PagedResult<ExtendedActionDto> getByRobotModelId(@RequestParam UUID robotModelId, @RequestParam(value = "page", defaultValue = "1") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        return extendedActionService.getExtendedActionByRobotModelId(robotModelId, page, size);
    }

    @PostMapping
    @Operation(summary = "Create new extended action")
    public ExtendedActionDto create(@RequestBody ExtendedActionDto extendedActionDto) {
        return extendedActionService.createExtendedAction(extendedActionDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update extended action by id")
    public ExtendedActionDto update(@PathVariable UUID id, @RequestBody ExtendedActionDto extendedActionDto) {
        return extendedActionService.updateExtendedAction(id, extendedActionDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch update extended action by id")
    public ExtendedActionDto patchUpdate(@PathVariable UUID id, @RequestBody ExtendedActionDto extendedActionDto) {
        return extendedActionService.patchUpdateExtendedAction(id, extendedActionDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete extended action by id")
    public String delete(@PathVariable UUID id) {
        return extendedActionService.deleteExtendedAction(id);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Change extended action status")
    public ExtendedActionDto changeStatus(@PathVariable UUID id, @RequestBody Integer status) {
        return extendedActionService.changeExtendedActionStatus(id, status);
    }
}
