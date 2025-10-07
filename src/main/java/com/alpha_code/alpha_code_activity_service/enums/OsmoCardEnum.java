package com.alpha_code.alpha_code_activity_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OsmoCardEnum {
    DELETED(0, "ĐÃ XÓA"),
    ACTIVE(1, "ĐANG HOẠT ĐỘNG");

    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (OsmoCardEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
