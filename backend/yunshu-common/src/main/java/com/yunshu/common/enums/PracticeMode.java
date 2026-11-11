package com.yunshu.common.enums;

import lombok.Getter;

/**
 * 练习模式枚举
 */
@Getter
public enum PracticeMode {
    SEQUENCE("sequence", "顺序"),
    RANDOM("random", "随机");

    private final String value;
    private final String label;

    PracticeMode(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static PracticeMode fromValue(String value) {
        for (PracticeMode m : values()) {
            if (m.value.equals(value)) return m;
        }
        return null;
    }
}
