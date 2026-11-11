package com.yunshu.common.enums;

import lombok.Getter;

/**
 * 题目状态枚举
 */
@Getter
public enum QuestionStatus {
    DRAFT("draft", "草稿"),
    PENDING_REVIEW("pending_review", "待审核"),
    PUBLISHED("published", "已上架"),
    OFFLINE("offline", "已下架"),
    REJECTED("rejected", "已驳回");

    private final String value;
    private final String label;

    QuestionStatus(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static QuestionStatus fromValue(String value) {
        for (QuestionStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
