package com.yunshu.common.enums;

import lombok.Getter;

/**
 * 业务错误码枚举
 */
@Getter
public enum ErrorCode {

    // 通用客户端错误
    PARAM_ERROR(40001, "参数校验失败"),
    PHONE_INVALID(40002, "手机号格式不正确"),
    PHONE_EXISTS(40003, "手机号已注册"),
    EMAIL_EXISTS(40004, "邮箱已被占用"),
    PASSWORD_WEAK(40005, "密码强度不足，需包含字母和数字"),
    NICKNAME_INVALID(40006, "昵称不合规（禁止纯数字）"),
    ANSWER_TOO_SHORT(40007, "简答题答案过短"),
    QUESTION_NOT_IN_INTERVIEW(40008, "题目不属于本场面试"),
    FILE_TOO_LARGE(40009, "头像文件过大（限 2MB）"),
    FILE_FORMAT_UNSUPPORTED(40010, "头像文件格式不支持"),
    CREDENTIALS_INVALID(40100, "用户名或密码错误"),
    ACCOUNT_DISABLED(40101, "账号已禁用"),
    PUBLISHED_QUESTION_EDIT_FORBIDDEN(40102, "已上架题目不可直接编辑，请先下架"),
    INVALID_OPERATION_STATUS(40103, "当前状态不可执行此操作"),
    REJECT_COMMENT_REQUIRED(40104, "驳回时必须填写备注"),
    RESERVED_ROLE_CODE(40105, "角色编码使用了保留名称"),

    // 认证相关
    UNAUTHORIZED(40100, "未登录或 Token 失效"),
    TOKEN_EXPIRED(40101, "Token 已过期，请刷新"),

    // 权限相关
    ACCESS_DENIED(40301, "无权执行此操作"),
    SUPER_ADMIN_PROTECTED(40302, "超级管理员不可编辑/删除"),

    // 资源相关
    CATALOG_NOT_FOUND_OR_EMPTY(40401, "阶段不存在或无上架题目"),
    WRONG_QUESTION_NOT_FOUND(40402, "题目不在你的错题本中"),

    // 冲突相关
    ACTIVE_INTERVIEW_EXISTS(40901, "存在活跃面试会话，请先完成"),
    SUBJECT_HAS_CATALOGS(40902, "学科下有关联阶段，无法删除"),
    CATALOG_HAS_PUBLISHED_QUESTIONS(40903, "存在上架题目，无法删除阶段/学科"),
    BV_ID_EXISTS(40904, "BV号在全库中已存在"),
    COURSE_ALREADY_IN_ROUTE(40905, "该课程已在路线中"),
    ROLE_HAS_USERS(40906, "角色下仍有在用用户，无法删除"),

    // 频控相关
    TOO_MANY_REQUESTS(42900, "操作过于频繁，请稍后再试"),
    DAILY_INTERVIEW_QUOTA_EXHAUSTED(42901, "今日面试次数已达上限"),

    // 服务端错误
    INTERNAL_ERROR(50000, "服务端内部错误"),
    AI_SERVICE_ERROR(50001, "AI 服务调用失败"),
    ;

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
