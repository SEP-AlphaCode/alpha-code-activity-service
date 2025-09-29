package com.alpha_code.alpha_code_activity_service.controller;

import com.alpha_code.alpha_code_activity_service.dto.DanceDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.service.DanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dances")
@RequiredArgsConstructor
@Tag(name = "Dances")
@Validated
public class DanceController {

    private final DanceService service;

    @GetMapping
    @Operation(summary = "Get all dances with pagination and optional filters")
    public PagedResult<DanceDto> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                        @RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "code", required = false) String code,
                                        @RequestParam(value = "description", required = false) String description,
                                        @RequestParam(value = "status", required = false) Integer status,
                                        @RequestParam(value = "robotModelId", required = false) UUID robotModelId){
        return service.getAll(page, size, name, code, description, status, robotModelId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dance by id")
    public DanceDto getById(@PathVariable UUID id){
        return service.getById(id);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get dance by code")
    public DanceDto getByCode(@PathVariable String code){
        return service.getDanceByCode(code);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get dance by name")
    public DanceDto getByName(@PathVariable String name){
        return service.getDanceByName(name);
    }

    @GetMapping("/robot-model/{robotModelId}")
    @Operation(summary = "Get dances by robot model id")
    public List<DanceDto> getAllByRobotModelId(@PathVariable UUID robotModelId){
        return service.getAllByRobotModelId(robotModelId);
    }

    @PostMapping
    @Operation(summary = "Create new dance")
    public DanceDto create(@RequestBody DanceDto dto){
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update dance by id")
    public DanceDto update(@PathVariable UUID id, @RequestBody DanceDto dto){
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch update dance by id")
    public DanceDto patch(@PathVariable UUID id, @RequestBody DanceDto dto){
        return service.patchUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete dance by id")
    public String delete(@PathVariable UUID id){
        return  service.delete(id);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Change dance status by id")
    public DanceDto changeStatus(@PathVariable UUID id, Integer status){
        return service.changeStatus(id, status);
    }
}
