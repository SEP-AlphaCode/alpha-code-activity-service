package com.alpha_code.alpha_code_activity_service.controller;

import com.alpha_code.alpha_code_activity_service.dto.ExpressionDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.service.ExpressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expressions")
@RequiredArgsConstructor
@Tag(name = "Expressions")
@Validated
public class ExpressionController {

    private final ExpressionService service;

    @GetMapping
    @Operation(summary = "Get all expressions with pagination and optional filters")
    public PagedResult<ExpressionDto> getAll (@RequestParam(value = "page", defaultValue = "1") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                              @RequestParam(value = "name", required = false) String name,
                                              @RequestParam(value = "code", required = false) String code,
                                              @RequestParam(value = "status", required = false) Integer status,
                                              @RequestParam(value = "robotModelId", required = false)UUID robotModelId){
        return service.getAll(page, size, name, code, status, robotModelId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expression by id")
    public ExpressionDto getById(@PathVariable UUID id){
        return service.getById(id);
    }

    @GetMapping("/robot-model/{robotModelId}")
    @Operation(summary = "Get expressions by robot model id")
    public List<ExpressionDto> getByRobotModelId(@PathVariable UUID robotModelId){
        return service.getByRobotModelId(robotModelId);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get expression by name")
    public ExpressionDto getByName(@PathVariable String name){
        return service.getByName(name);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get expression by code")
    public ExpressionDto getByCode(@PathVariable String code){
        return service.getByCode(code);
    }

    @PostMapping
    @Operation(summary = "Create new expression")
    public ExpressionDto create(@RequestBody ExpressionDto dto){
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update expression by id")
    public ExpressionDto update(@PathVariable UUID id, @RequestBody ExpressionDto dto){
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch update expression by id")
    public ExpressionDto patch(@PathVariable UUID id, @RequestBody ExpressionDto dto){
        return service.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expression by id")
    public String delete(@PathVariable UUID id){
        return service.delete(id);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Change expression status by id")
    public ExpressionDto changeStatus(@PathVariable UUID id, @RequestBody Integer status){
        return service.changeStatus(id, status);
    }
}
