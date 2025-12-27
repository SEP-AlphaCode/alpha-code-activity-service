package com.alpha_code.alpha_code_activity_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TypeEnum {
    WEEK(1, "YẾU"),
    MEDIUM(2, "TRUNG BÌNH"),
    STRONG(3, "MẠNH");

    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (TypeEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "Không xác định";
    }
}
