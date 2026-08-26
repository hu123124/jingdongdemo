package com.example.jingdongdemo.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.dto.PaymentRequest;
import com.example.jingdongdemo.entity.Payment;
import com.example.jingdongdemo.service.CategoryService;
import com.example.jingdongdemo.service.PaymentService;
import com.example.jingdongdemo.vo.CategoryVO;
import com.example.jingdongdemo.vo.PaymentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public R<PaymentVO> postPayment(@RequestBody PaymentRequest paymentRequest){
        return R.ok(paymentService.postPayment(paymentRequest));
    }
    // 支付回调（模拟——直接返回成功，实际支付已在 pay() 里完成）
    @PostMapping("/callback")
    public R<Void> callback() {
        return R.ok();
    }
    // 查询支付状态
    @GetMapping("/{payNo}")
    public R<PaymentVO> query(@PathVariable String payNo) {

        return R.ok(paymentService.query(payNo));
    }
}
