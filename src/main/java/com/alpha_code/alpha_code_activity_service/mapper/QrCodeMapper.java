package com.alpha_code.alpha_code_activity_service.mapper;


import com.alpha_code.alpha_code_activity_service.dto.QrCodeDto;
import com.alpha_code.alpha_code_activity_service.entity.QrCode;

public class QrCodeMapper {

    public static QrCodeDto toDto(QrCode qrCode) {
        if (qrCode == null) return null;

        QrCodeDto dto = new QrCodeDto();
        dto.setId(qrCode.getId());
        dto.setName(qrCode.getName());
        dto.setColor(qrCode.getColor());
        dto.setQrCode(qrCode.getQrCode());
        dto.setStatus(qrCode.getStatus());
        dto.setCreatedDate(qrCode.getCreatedDate());
        dto.setLastUpdated(qrCode.getLastUpdated());
        dto.setImageUrl(qrCode.getImageUrl());
        dto.setActivityId(qrCode.getActivityId());
        dto.setAccountId(qrCode.getAccountId());
        dto.setActivityName(qrCode.getActivity() != null ? qrCode.getActivity().getName() : null);

        return dto;
    }

    public static QrCode toEntity(QrCodeDto dto) {
        if (dto == null) return null;

        QrCode entity = new QrCode();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setQrCode(dto.getQrCode());
        entity.setColor(dto.getColor());
        entity.setStatus(dto.getStatus());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setLastUpdated(dto.getLastUpdated());
        entity.setImageUrl(dto.getImageUrl());
        entity.setActivityId(dto.getActivityId());
        entity.setAccountId(dto.getAccountId());

        return entity;
    }
}
