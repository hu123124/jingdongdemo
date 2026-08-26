package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.AddressRequest;
import com.example.jingdongdemo.dto.OrderPageRequest;
import com.example.jingdongdemo.dto.OrderRequest;
import com.example.jingdongdemo.vo.*;

import java.util.List;

public interface OrderService {


    OrderConfirmVO OrderConfirm();

    OrderVO createOrder(OrderRequest orderRequest);

    PageResultVO<OrderListVO> orderList(OrderPageRequest orderPageRequest);

    OrderDetailVO orderDetail(String orderNo);

    void cancelOrder(String orderNo);

    void receiveOrder(String orderNo);
    /**
     * admin
     *
     */
    // OrderService 加
    PageResultVO<OrderListVO> adminOrderList(Integer pageNum, Integer pageSize, Integer status, String orderNo);
    void shipOrder(String orderNo, String logisticsNo, String logisticsCompany);
}
