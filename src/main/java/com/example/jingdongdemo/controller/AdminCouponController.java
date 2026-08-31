package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.entity.Coupon;
import com.example.jingdongdemo.service.CouponService;
import com.example.jingdongdemo.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * B端 - 优惠券管理
 */
@RestController @RequiredArgsConstructor
@RequestMapping("/api/admin/v1/coupons")
public class AdminCouponController {

    private final CouponService couponService;

    /** B端 - 优惠券列表 */
    @GetMapping
    public R<PageResultVO<Coupon>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "15") Integer pageSize) {
        return R.ok(couponService.adminList(pageNum, pageSize));
    }

    /** B端 - 优惠券详情 */
    @GetMapping("/{id}")
    public R<Coupon> detail(@PathVariable Long id) {
        return R.ok(couponService.adminDetail(id));
    }

    /** B端 - 新增优惠券 */
    @PostMapping
    public R<Void> create(@RequestBody Map<String, Object> body) {
        couponService.adminCreate(body);
        return R.ok();
    }

    /** B端 - 修改优惠券 */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        couponService.adminUpdate(id, body);
        return R.ok();
    }

    /** B端 - 删除优惠券 */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        couponService.adminDelete(id);
        return R.ok();
    }

    /** B端 - 启用/禁用优惠券 */
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        couponService.adminUpdateStatus(id, body.get("status"));
        return R.ok();
    }
}
