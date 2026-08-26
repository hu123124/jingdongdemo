package com.example.jingdongdemo.common;

import lombok.Data;

/**
 * 统一返回格式 —— 接口文档里定义的 {code, message, data} 结构
 * 前端只需要认这个格式，不管请求成功还是失败
 */
@Data
public class R<T> {
    private int code;
    private String message;
    private T data;

    // 成功（带数据）
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = 200;
        r.message = "success";
        r.data = data;
        return r;
    }

    // 成功（无数据）
    public static <T> R<T> ok() {
        return ok(null);
    }

    // 失败
    public static <T> R<T> error(int code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        r.data = null;
        return r;
    }
}
