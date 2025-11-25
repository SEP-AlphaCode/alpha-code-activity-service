package com.alpha_code.alpha_code_activity_service.controller;

import com.alpha_code.alpha_code_activity_service.dto.OsmoCardDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.service.OsmoCardService;
import com.alpha_code.alpha_code_activity_service.validation.OnCreate;
import com.alpha_code.alpha_code_activity_service.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/osmo-cards")
@RequiredArgsConstructor
@Tag(name = "OsmoCards")
public class OsmoCardController {
    private final OsmoCardService service;

    @GetMapping
    @Operation(summary = "Get all osmo cards with pagination and optional status filter")
    public PagedResult<OsmoCardDto> getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) Integer status) {
        return service.getAll(page, size, status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get osmo card by id")
    public OsmoCardDto getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Parent', 'ROLE_Children')")
    @Operation(summary = "Create new osmo card")
    public OsmoCardDto create(@Validated(OnCreate.class) @RequestBody OsmoCardDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Parent', 'ROLE_Children')")
    @Operation(summary = "Update osmo card by id")
    public OsmoCardDto update(@PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody OsmoCardDto dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Parent', 'ROLE_Children')")
    @Operation(summary = "Patch update osmo card by id")
    public OsmoCardDto patchUpdate(@PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody OsmoCardDto dto) {
        return service.patchUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Parent', 'ROLE_Children')")
    @Operation(summary = "Delete osmo card by id")
    public String delete(@PathVariable UUID id) {
        return service.delete(id);
    }
}
