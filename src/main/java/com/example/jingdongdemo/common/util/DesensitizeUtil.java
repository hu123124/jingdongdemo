package com.example.jingdongdemo.common.util;

/**
 * 脱敏工具类（通用工具不放业务层，放这里全局复用）
 */
public final class DesensitizeUtil {

    /** 工具类不允许 new */
    private DesensitizeUtil() {
    }

    /**
     * 手机号脱敏：正常 11 位 → 138****5678；
     * null/空 → ""；长度 < 7 的脏数据 → 整体打码，绝不抛异常
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) return "";
        if (phone.length() < 7) return "****";
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}