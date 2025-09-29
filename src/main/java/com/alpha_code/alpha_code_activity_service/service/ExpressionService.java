package com.alpha_code.alpha_code_activity_service.service;

import com.alpha_code.alpha_code_activity_service.dto.ExpressionDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;

import java.util.List;
import java.util.UUID;

public interface ExpressionService {
    PagedResult<ExpressionDto> getAll (int page, int size, String name, String code, Integer status, UUID robotModelId);

    ExpressionDto getById(UUID id);

    ExpressionDto create(ExpressionDto dto);

    ExpressionDto update(UUID id, ExpressionDto dto);

    ExpressionDto patch(UUID id, ExpressionDto dto);

    String delete(UUID id);

    ExpressionDto getByName(String name);

    ExpressionDto getByCode(String code);

    List<ExpressionDto> getByRobotModelId(UUID robotModelId);

    ExpressionDto changeStatus(UUID id, Integer status);
}
