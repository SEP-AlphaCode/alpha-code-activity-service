package com.alpha_code.alpha_code_activity_service.service;

import com.alpha_code.alpha_code_activity_service.dto.ActivityDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.dto.QrCodeDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface QrCodeService {
    PagedResult<QrCodeDto> getAll(int page, int size, Integer status, UUID accountId);

    QrCodeDto getById(UUID id);

    ActivityDto getByQrImage(MultipartFile file);

    QrCodeDto create(QrCodeDto QrCodeDto);

    QrCodeDto update(UUID id, QrCodeDto QrCodeDto);

    QrCodeDto patchUpdate(UUID id, QrCodeDto QrCodeDto);

    String delete(UUID id);
    String disable(UUID id);
    QrCodeDto getByCode(String code);

    QrCodeDto changeStatus(UUID id, Integer status);
}
