package com.alpha_code.alpha_code_activity_service.service;

import com.alpha_code.alpha_code_activity_service.dto.ActionDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ActionService {
    PagedResult<ActionDto> searchActions(int page, int size,
                                         String name,
                                         String code,
                                         String description,
                                         Integer status,
                                         Boolean canInterrupt,
                                         Integer duration);

    ActionDto getActionById(UUID id);

    ActionDto getActionByName(String name);

    ActionDto getActionByCode(String code);

    ActionDto getActionByRobotModelId(UUID robotModelId);

    ActionDto createAction(ActionDto actionDto);

    ActionDto updateAction(UUID id, ActionDto actionDto);

    ActionDto patchUpdateAction(UUID id, ActionDto actionDto);

    ActionDto changeActionStatus(UUID id, Integer status);

    String deleteAction(UUID id);
}
