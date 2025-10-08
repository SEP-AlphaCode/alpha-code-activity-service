package com.alpha_code.alpha_code_activity_service.controller;

import com.alpha_code.alpha_code_activity_service.dto.JoystickDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.service.JoystickService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/joysticks")
@RequiredArgsConstructor
@Tag(name = "Joysticks", description = "Joystick management APIs")
public class JoystickController {

    private final JoystickService joystickService;

    // -----------------------------
    // GET by accountId + robotId
    // -----------------------------
    @GetMapping("/by-account-robot")
    @Operation(summary = "Lấy joystick theo accountId và robotId (có cache Redis)")
    public List<JoystickDto> getByAccountIdAndRobotId(
            @RequestParam UUID accountId,
            @RequestParam UUID robotId
    ) {
        return joystickService.getByAccountIdAndRobotId(accountId, robotId);
    }

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    @Operation(summary = "Tạo mới joystick")
    public JoystickDto create(@RequestBody JoystickDto joystickDto) {
        return joystickService.create(joystickDto);
    }

    // -----------------------------
    // UPDATE (PUT)
    // -----------------------------
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật toàn bộ thông tin joystick (PUT)")
    public JoystickDto update(@PathVariable UUID id, @RequestBody JoystickDto joystickDto) {
        return joystickService.update(id, joystickDto);
    }

    // -----------------------------
    // PATCH (chỉ cập nhật trường thay đổi)
    // -----------------------------
    @PatchMapping("/{id}")
    @Operation(summary = "Cập nhật một phần thông tin joystick (PATCH)")
    public JoystickDto patch(@PathVariable UUID id, @RequestBody JoystickDto joystickDto) {
        return joystickService.patch(id, joystickDto);
    }

    // -----------------------------
    // DELETE (soft delete)
    // -----------------------------
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm joystick (chỉ cập nhật trạng thái)")
    public void delete(@PathVariable UUID id) {
        joystickService.delete(id);
    }
}
