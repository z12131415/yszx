package com.yunshu.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 脱敏工具
 */
public class DesensitizeUtil {

    /**
     * 手机号脱敏：13812345678 -> 138****5678
     */
    public static String desensitizePhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 邮箱脱敏：zhangsan@example.com -> z***@example.com
     */
    public static String desensitizeEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@", 2);
        String local = parts[0];
        if (local.length() <= 1) return local + "***@" + parts[1];
        return local.charAt(0) + "***@" + parts[1];
    }
}
