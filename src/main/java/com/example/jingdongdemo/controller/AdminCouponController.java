package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.entity.Coupon;
import com.example.jingdongdemo.mapper.CouponMapper;
import com.example.jingdongdemo.vo.PageResultVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * B端 - 优惠券管理
 */
@RestController @RequiredArgsConstructor
@RequestMapping("/api/admin/v1/coupons")
public class AdminCouponController {

    private final CouponMapper couponMapper;

    /** B端 - 优惠券列表 */
    @GetMapping
    public R<PageResultVO<Coupon>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "15") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        java.util.List<Coupon> list = couponMapper.selectAll();
        PageInfo<Coupon> info = new PageInfo<>(list);
        PageResultVO<Coupon> result = new PageResultVO<>();
        result.setList(list); result.setTotal(info.getTotal());
        result.setPageNum(pageNum); result.setPageSize(pageSize); result.setPages(info.getPages());
        return R.ok(result);
    }

    /** B端 - 优惠券详情 */
    @GetMapping("/{id}")
    public R<Coupon> detail(@PathVariable Long id) {
        return R.ok(couponMapper.getByCouponId2(id));
    }

    /** B端 - 新增优惠券 */
    @PostMapping
    public R<Void> create(@RequestBody Map<String, Object> body) {
        Coupon c = new Coupon();
        c.setName((String) body.get("name"));
        c.setType(Integer.valueOf(body.get("type").toString()));
        c.setDiscountValue(new BigDecimal(body.get("discountValue").toString()));
        c.setMinAmount(new BigDecimal(body.getOrDefault("minAmount", "0").toString()));
        c.setTotalCount(Integer.valueOf(body.get("totalCount").toString()));
        c.setReceiveCount(0);
        c.setStatus(1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        c.setStartTime(LocalDateTime.parse((String) body.get("startTime"), fmt));
        c.setEndTime(LocalDateTime.parse((String) body.get("endTime"), fmt));
        couponMapper.insertCoupon(c);
        return R.ok();
    }

    /** B端 - 修改优惠券 */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Coupon c = new Coupon();
        c.setId(id);
        c.setName((String) body.get("name"));
        c.setType(Integer.valueOf(body.get("type").toString()));
        c.setDiscountValue(new BigDecimal(body.get("discountValue").toString()));
        c.setMinAmount(new BigDecimal(body.getOrDefault("minAmount", "0").toString()));
        c.setTotalCount(Integer.valueOf(body.get("totalCount").toString()));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        c.setStartTime(LocalDateTime.parse((String) body.get("startTime"), fmt));
        c.setEndTime(LocalDateTime.parse((String) body.get("endTime"), fmt));
        couponMapper.updateCoupon(c);
        return R.ok();
    }

    /** B端 - 删除优惠券 */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        couponMapper.deleteCoupon(id);
        return R.ok();
    }

    /** B端 - 启用/禁用优惠券 */
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        couponMapper.updateStatus(id, body.get("status"));
        return R.ok();
    }
}
