package com.example.jingdongdemo.common.enums;

public enum OrderStatusEnum {
    PENDING_PAY(0, "待付款"),
    PENDING_SHIP(1, "待发货"),
    PENDING_RECEIVE(2, "待收货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消"),
    REFUNDED(5, "已退款");

    private final int code;
    private final String text;

    OrderStatusEnum(int code, String text) { this.code = code; this.text = text; }

    public static String getText(int code) {
        for (OrderStatusEnum e : values()) {
            if (e.code == code) return e.text;
        }
        return null;
    }
}