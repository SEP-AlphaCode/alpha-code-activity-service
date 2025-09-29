package com.alpha_code.alpha_code_activity_service.service;

import com.alpha_code.alpha_code_activity_service.dto.DanceDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;

import java.util.List;
import java.util.UUID;

public interface DanceService {
    PagedResult<DanceDto> getAll(int page, int size, String name, String code, Integer status, UUID robotModelId);

    DanceDto getById(UUID id);

    DanceDto getDanceByCode(String code);

    DanceDto getDanceByName(String name);

    List<DanceDto> getAllByRobotModelId(UUID robotModelId);

    DanceDto create(DanceDto dto);

    DanceDto update(UUID id, DanceDto dto);

    DanceDto patchUpdate(UUID id, DanceDto dto);

    String delete(UUID id);

    DanceDto changeStatus(UUID id, Integer status);
}
