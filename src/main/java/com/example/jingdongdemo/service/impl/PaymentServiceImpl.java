package com.example.jingdongdemo.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.example.jingdongdemo.dto.AddressRequest;
import com.example.jingdongdemo.dto.PaymentRequest;
import com.example.jingdongdemo.entity.Address;
import com.example.jingdongdemo.entity.Order;
import com.example.jingdongdemo.entity.Payment;
import com.example.jingdongdemo.mapper.AddressMapper;
import com.example.jingdongdemo.mapper.OrderMapper;
import com.example.jingdongdemo.mapper.PaymentMapper;
import com.example.jingdongdemo.service.AddressService;
import com.example.jingdongdemo.service.PaymentService;
import com.example.jingdongdemo.vo.AddressVO;
import com.example.jingdongdemo.vo.PaymentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderMapper orderMapper;
    private final PaymentMapper paymentMapper;


    @Transactional
    @Override
    public PaymentVO postPayment(PaymentRequest req) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 1. 查订单，确认是当前用户的、状态为待付款
        Order order = orderMapper.getByNo(req.getOrderNo());
        if (order == null) throw new RuntimeException("订单不存在");
        if (!order.getUserId().equals(userId)) throw new RuntimeException("无权操作");
        if (order.getStatus() != 0) throw new RuntimeException("订单状态不支持支付");

        // 2. 生成支付流水号
        String payNo = "PAY" + IdUtil.getSnowflakeNextId();

        // 3. 插入支付记录（模拟：直接支付成功）
        Payment payment = Payment.builder()
                .orderNo(req.getOrderNo())
                .userId(userId)
                .payNo(payNo)
                .payChannel(req.getPayChannel())
                .payAmount(order.getPayAmount())
                .status(1)           // 模拟：直接成功
                .payTime(LocalDateTime.now())
                .build();
        paymentMapper.insert(payment);

        // 4. 更新订单状态
        orderMapper.paySuccess(req.getOrderNo());

        // 5. 返回支付信息
        PaymentVO vo = new PaymentVO();
        vo.setPayNo(payNo);
        vo.setPayChannel(req.getPayChannel());
        vo.setPayAmount(order.getPayAmount());
        vo.setPayUrl("");  // 模拟支付无链接
        vo.setExpireTime(LocalDateTime.now().plusMinutes(30));
        return vo;
    }

    @Override
    public PaymentVO query(String payNo) {
        Payment payment = paymentMapper.getByPayNo(payNo);
        PaymentVO vo = new PaymentVO();
        BeanUtil.copyProperties(payment, vo);
        return vo;
    }
}
