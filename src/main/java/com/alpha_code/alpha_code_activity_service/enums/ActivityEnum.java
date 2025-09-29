package com.alpha_code.alpha_code_activity_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityEnum {
    DELETED(0, "DELETED"),
    ACTIVE(1, "ACTIVE");

    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (ActivityEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "Không xác định";
    }
}
