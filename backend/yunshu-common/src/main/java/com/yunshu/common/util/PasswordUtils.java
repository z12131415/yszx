package com.yunshu.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具（封装 BCrypt）
 */
public class PasswordUtils {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 生成 8~12 位随机临时密码
     */
    public static String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        int len = 8 + random.nextInt(5); // 8~12
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        // 确保含字母和数字
        if (!sb.toString().matches(".*[A-Za-z].*") || !sb.toString().matches(".*\\d.*")) {
            sb.setCharAt(0, 'a');
            sb.setCharAt(1, (char) ('0' + (System.currentTimeMillis() % 10)));
        }
        return sb.toString();
    }
}
