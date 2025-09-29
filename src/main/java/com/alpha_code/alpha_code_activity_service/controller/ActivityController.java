package com.alpha_code.alpha_code_activity_service.controller;

import com.alpha_code.alpha_code_activity_service.dto.ActivityDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Tag(name = "Activities")
@Validated
public class ActivityController {

    private final ActivityService service;

    @GetMapping
    @Operation(summary = "Get all activities with pagination and optional filters")
    public PagedResult<ActivityDto> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size,
                                           @RequestParam(value = "keyword", required = false) String keyword,
                                           @RequestParam(value = "accountId", required = false ) UUID accountId,
                                           @RequestParam(value = "status", required = false) Integer status){
        return service.getAll(page, size, keyword, accountId, status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get activity by id")
    public ActivityDto getById(@PathVariable UUID id){
        return service.getActivityById(id);
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get activities by account id")
    public List<ActivityDto> getByAccountId(@PathVariable UUID accountId){
        return service.getByAccountId(accountId);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get activities by type")
    public List<ActivityDto> getByType(@PathVariable String type){
        return service.getByType(type);
    }

    @PostMapping
    @Operation(summary = "Create new activity")
    public ActivityDto create(@RequestBody ActivityDto dto){
        return  service.createActivity(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update activity by id")
    public ActivityDto update(@PathVariable UUID id,@RequestBody ActivityDto dto){
        return service.updateActivity(id, dto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch update activity by id")
    public ActivityDto patchUpdate(@PathVariable UUID id,@RequestBody ActivityDto dto){
        return service.patchUpdateActivity(id, dto);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Change activity status by id")
    public  ActivityDto changeStatus(@PathVariable UUID id,@RequestBody Integer status){
        return service.changeActivityStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete activity by id")
    public String delete(@PathVariable UUID id){
        return service.deleteActivity(id);
    }
}
