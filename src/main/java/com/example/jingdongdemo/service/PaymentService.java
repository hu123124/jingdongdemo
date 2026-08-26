package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.CartBatchDTO;
import com.example.jingdongdemo.dto.CartRequest;
import com.example.jingdongdemo.dto.PaymentRequest;
import com.example.jingdongdemo.vo.CartListVO;
import com.example.jingdongdemo.vo.PaymentVO;

public interface PaymentService {


    PaymentVO postPayment(PaymentRequest paymentRequest);

    PaymentVO query(String payNo);
}
