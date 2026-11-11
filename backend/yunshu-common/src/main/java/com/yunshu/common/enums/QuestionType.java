package com.yunshu.common.enums;

import lombok.Getter;

/**
 * 题目类型枚举
 */
@Getter
public enum QuestionType {
    SINGLE_CHOICE("single_choice", "单选题"),
    MULTIPLE_CHOICE("multiple_choice", "多选题"),
    JUDGE("judge", "判断题"),
    ESSAY("essay", "简答题");

    private final String value;
    private final String label;

    QuestionType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static QuestionType fromValue(String value) {
        for (QuestionType type : values()) {
            if (type.value.equals(value)) return type;
        }
        return null;
    }
}
