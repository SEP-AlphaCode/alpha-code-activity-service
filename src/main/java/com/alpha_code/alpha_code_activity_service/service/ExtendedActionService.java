package com.alpha_code.alpha_code_activity_service.service;

import com.alpha_code.alpha_code_activity_service.dto.ExtendedActionDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;

import java.util.UUID;

public interface ExtendedActionService {
    String deleteExtendedAction(UUID id);
    ExtendedActionDto changeExtendedActionStatus(UUID id, Integer status);
    ExtendedActionDto patchUpdateExtendedAction(UUID id, ExtendedActionDto extendedActionDto);
    ExtendedActionDto updateExtendedAction(UUID id, ExtendedActionDto extendedActionDto);
    ExtendedActionDto createExtendedAction(ExtendedActionDto extendedActionDto);
    PagedResult<ExtendedActionDto> getExtendedActionByRobotModelId(UUID robotModelId, int page, int size);
    ExtendedActionDto getExtendedActionByCode(String code);
    ExtendedActionDto getExtendedActionByName(String name);
    ExtendedActionDto getExtendedActionById(UUID id);
    PagedResult<ExtendedActionDto> searchExtendedActions(int page, int size, String searchTerm);
}
