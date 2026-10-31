package com.youkeda.shede.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// 密码用固定盐 md5 存，这里够用
public final class MD5Utils {

    private static final String SALT = "shede&youkeda#2026"; // 盐写死了

    private static final char[] HEX = "0123456789abcdef".toCharArray(); // 转小写十六进制用

    // 工具类不让 new
    private MD5Utils() {
    }

    // 出 32 位小写，中文按 utf8 取字节
    public static String md5(String text) {
        if (text == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            char[] chars = new char[bytes.length * 2];
            for (int i = 0; i < bytes.length; i++) {
                // 一个字节拆成两个十六进制字符
                int value = bytes[i] & 0xFF;
                chars[i * 2] = HEX[value >>> 4];
                chars[i * 2 + 1] = HEX[value & 0x0F];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available", e);
        }
    }

    // 同一个密码算出来永远一样
    public static String encrypt(String rawPassword) {
        return md5(SALT + rawPassword + SALT);
    }

    // 生产要随机盐，别学这里
    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        return encrypt(rawPassword).equalsIgnoreCase(storedPassword);
    }
}
