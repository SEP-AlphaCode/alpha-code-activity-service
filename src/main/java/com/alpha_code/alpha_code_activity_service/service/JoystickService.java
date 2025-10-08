package com.alpha_code.alpha_code_activity_service.service;

import com.alpha_code.alpha_code_activity_service.dto.JoystickDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;

import java.util.List;
import java.util.UUID;

public interface JoystickService {
    List<JoystickDto> getByAccountIdAndRobotId(UUID accountId, UUID robotId);
    JoystickDto create(JoystickDto joystickDto);
    JoystickDto update(UUID id, JoystickDto joystickDto);
    JoystickDto patch(UUID id, JoystickDto joystickDto);
    void delete(UUID id);
}
