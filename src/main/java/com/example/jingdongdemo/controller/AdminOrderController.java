package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.service.OrderService;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.OrderDetailVO;
import com.example.jingdongdemo.vo.OrderListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/admin/v1/orders")
public class AdminOrderController {
    private final OrderService orderService;

    // 订单列表（查所有用户）
    @GetMapping
    public R<PageResultVO<OrderListVO>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) String orderNo) {
        return R.ok(orderService.adminOrderList(pageNum, pageSize, status, orderNo));
    }

    // 订单详情
    @GetMapping("/{orderNo}")
    public R<OrderDetailVO> detail(@PathVariable String orderNo) {
        return R.ok(orderService.orderDetail(orderNo));
    }

    // 发货
    @PutMapping("/{orderNo}/ship")
    public R<Void> ship(@PathVariable String orderNo, @RequestBody Map<String, String> body) {
        orderService.shipOrder(orderNo, body.get("logisticsNo"), body.get("logisticsCompany"));
        return R.ok();
    }
}
