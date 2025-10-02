package com.alpha_code.alpha_code_activity_service.controller;

import com.alpha_code.alpha_code_activity_service.dto.ActivityDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.dto.QrCodeDto;
import com.alpha_code.alpha_code_activity_service.service.QrCodeService;
import com.alpha_code.alpha_code_activity_service.validation.OnCreate;
import com.alpha_code.alpha_code_activity_service.validation.OnUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/qr-codes")
@RequiredArgsConstructor
@Tag(name = "QrCodes")
public class QrCodeController {
    private final QrCodeService service;

    @GetMapping
    @Operation(summary = "Get all Qr codes with pagination and optional status filter")
    public PagedResult<QrCodeDto> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int size,
                                         @RequestParam(value = "status", required = false) Integer status) {
        return service.getAll(page, size, status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Qr code by id")
    public QrCodeDto getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("by-code/{code}")
    @Operation(summary = "Get Qr code by code")
    public QrCodeDto getByCode(@PathVariable String code) {
        return service.getByCode(code);
    }

    @PostMapping(value = "/by-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Get Qr code by image")
    public ActivityDto getByImage(@RequestPart("image") MultipartFile image) {
        return service.getByQrImage(image);
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Teacher')")
    @Operation(summary = "Create new Qr code")
    public QrCodeDto create(@Validated(OnCreate.class) @RequestBody QrCodeDto requestDto) {
//        QrCodeDto QrCodeDto = new QrCodeDto();
//        QrCodeDto.setName(requestDto.getName());
//        QrCodeDto.setQrCode(requestDto.getQrCode());
//        QrCodeDto.setActivityId(requestDto.getActivityId());
//        QrCodeDto.setAccountId(requestDto.getAccountId());
//
//        return QrCodeService.create(QrCodeDto);
        return service.create(requestDto);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Teacher')")
    @Operation(summary = "Update QrCode")
    public QrCodeDto update(@PathVariable UUID id, @Validated(OnCreate.class) @RequestBody QrCodeDto QrCodeDto) throws JsonProcessingException {
        return service.update(id, QrCodeDto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Teacher')")
    @Operation(summary = "Patch update QrCode")
    public QrCodeDto patchUpdate(@PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody QrCodeDto QrCodeDto) {
        return service.patchUpdate(id, QrCodeDto);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Teacher')")
    @Operation(summary = "Update QrCode status")
    public QrCodeDto updateStatus(@PathVariable UUID id, @Validated(OnUpdate.class) @RequestParam Integer status) {
        return service.changeStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Teacher')")
    @Operation(summary = "Delete QrCode by id")
    public String delete(@PathVariable UUID id) {
        return service.delete(id);
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Teacher')")
    @Operation(summary = "Disable QrCode by id")
    public String disable(@PathVariable UUID id) {
        return service.disable(id);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Teacher')")
    @Operation(summary = "Change QrCode status")
    public QrCodeDto changeStatus(@PathVariable UUID id, @RequestParam Integer status) {
        return service.changeStatus(id, status);
    }
}
