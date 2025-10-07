package com.alpha_code.alpha_code_activity_service.service;

import com.alpha_code.alpha_code_activity_service.dto.JoystickDto;

import java.util.UUID;

public interface JoystickService {
    JoystickDto getByAccountIdAndRobotId(UUID accountId, UUID robotId);
    JoystickDto create(JoystickDto joystickDto);
    JoystickDto update(UUID id, JoystickDto joystickDto);
    JoystickDto patch(UUID id, JoystickDto joystickDto);
    void delete(UUID id);
}
