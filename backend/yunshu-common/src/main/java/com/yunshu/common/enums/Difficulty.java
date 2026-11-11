package com.yunshu.common.enums;

import lombok.Getter;

/**
 * 难度枚举
 */
@Getter
public enum Difficulty {
    EASY("easy", "简单"),
    MEDIUM("medium", "中等"),
    HARD("hard", "困难");

    private final String value;
    private final String label;

    Difficulty(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static Difficulty fromValue(String value) {
        for (Difficulty d : values()) {
            if (d.value.equals(value)) return d;
        }
        return null;
    }
}
