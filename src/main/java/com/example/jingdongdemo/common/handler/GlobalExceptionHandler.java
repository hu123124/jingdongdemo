package com.example.jingdongdemo.common.handler;

import com.example.jingdongdemo.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器 — 把所有异常转成统一 R 格式返回
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 — 返回业务状态码 */
    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e) {
        log.error("业务执行异常: {}", e.getMessage());
        return R.error(500, e.getMessage());
    }

    /** 兜底 — 未预料的异常 */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("未知异常", e);
        return R.error(500, "服务器内部未知错误");
    }
}