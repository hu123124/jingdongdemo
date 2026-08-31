package com.example.jingdongdemo.service.impl;


import com.example.jingdongdemo.dto.CartBatchDTO;
import com.example.jingdongdemo.dto.CartRequest;
import com.example.jingdongdemo.entity.Cart;
import com.example.jingdongdemo.entity.Coupon;
import com.example.jingdongdemo.entity.UserCoupon;
import com.example.jingdongdemo.mapper.CartMapper;
import com.example.jingdongdemo.mapper.CouponMapper;
import com.example.jingdongdemo.service.CartService;
import com.example.jingdongdemo.service.CouponService;
import com.example.jingdongdemo.vo.CartListVO;
import com.example.jingdongdemo.vo.CartVO;
import com.example.jingdongdemo.vo.CouponVO;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.UserCouponVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;

    @Override
    public List<CouponVO> getAvailableCoupon() {
        return couponMapper.getAvailableCoupon((Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Transactional
    @Override
    public void receiveCoupon(Long couponId) {
        Long userId = (Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Coupon coupon =couponMapper.getByCouponId(couponId,userId);
        if(coupon!=null){
            UserCoupon userCoupon=UserCoupon.builder()
                    .userId(userId)
                    .couponId(coupon.getId())
                    .status(0)
                    .build();
            couponMapper.receiveCoupon(userCoupon);
            couponMapper.incrReceiveCount(couponId);
            return;
        }
        throw new RuntimeException("领取失败");
    }

    @Override
    public List<UserCouponVO> getMineCoupon() {
        return couponMapper.getMineCoupon((Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    // ==================== B端 ====================

    /**
     * B端 - 优惠券列表（分页）
     */
    @Override
    public PageResultVO<Coupon> adminList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Coupon> list = couponMapper.selectAll();
        PageInfo<Coupon> info = new PageInfo<>(list);
        PageResultVO<Coupon> result = new PageResultVO<>();
        result.setList(list); result.setTotal(info.getTotal());
        result.setPageNum(pageNum); result.setPageSize(pageSize); result.setPages(info.getPages());
        return result;
    }

    /**
     * B端 - 优惠券详情
     */
    @Override
    public Coupon adminDetail(Long id) {
        return couponMapper.getByCouponId2(id);
    }

    /**
     * B端 - 新增优惠券
     */
    @Override
    public void adminCreate(Map<String, Object> body) {
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
    }

    /**
     * B端 - 修改优惠券
     */
    @Override
    public void adminUpdate(Long id, Map<String, Object> body) {
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
    }

    /**
     * B端 - 删除优惠券
     */
    @Override
    public void adminDelete(Long id) {
        couponMapper.deleteCoupon(id);
    }

    /**
     * B端 - 启用/禁用优惠券
     */
    @Override
    public void adminUpdateStatus(Long id, Integer status) {
        couponMapper.updateStatus(id, status);
    }
}
