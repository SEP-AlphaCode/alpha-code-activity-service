package com.alpha_code.alpha_code_activity_service.repository;

import com.alpha_code.alpha_code_activity_service.entity.Joystick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JoystickRepository extends JpaRepository<Joystick, UUID> {
    List<Joystick> findListByAccountIdAndRobotIdAndStatus(UUID accountId, UUID robotId, Integer status);

    Optional<Joystick> findByAccountIdAndRobotIdAndButtonCodeAndStatus(UUID accountId, UUID robotId, String buttonCode, Integer status);
}
