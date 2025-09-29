package com.alpha_code.alpha_code_activity_service.controller;

import com.alpha_code.alpha_code_activity_service.dto.ActivityDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.service.ActivityService;
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
    public PagedResult<ActivityDto> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size,
                                           @RequestParam(value = "keyword", required = false) String keyword,
                                           @RequestParam(value = "accountId", required = false ) UUID accountId,
                                           @RequestParam(value = "status", required = false) Integer status){
        return service.getAll(page, size, keyword, accountId, status);
    }

    @GetMapping("/{id}")
    public ActivityDto getById(@PathVariable UUID id){
        return service.getActivityById(id);
    }

    @GetMapping("/account/{accountId}")
    public List<ActivityDto> getByAccountId(@PathVariable UUID accountId){
        return service.getByAccountId(accountId);
    }

    @GetMapping("/type/{type}")
    public List<ActivityDto> getByType(@PathVariable String type){
        return service.getByType(type);
    }

    @PostMapping
    public ActivityDto create(@RequestBody ActivityDto dto){
        return  service.createActivity(dto);
    }

    @PutMapping("/{id}")
    public ActivityDto update(@PathVariable UUID id,@RequestBody ActivityDto dto){
        return service.updateActivity(id, dto);
    }

    @PatchMapping("/{id}")
    public ActivityDto patchUpdate(@PathVariable UUID id,@RequestBody ActivityDto dto){
        return service.patchUpdateActivity(id, dto);
    }

    @PatchMapping("/{id}/change-status")
    public  ActivityDto changeStatus(@PathVariable UUID id,@RequestBody Integer status){
        return service.changeActivityStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id){
        return service.deleteActivity(id);
    }
}
