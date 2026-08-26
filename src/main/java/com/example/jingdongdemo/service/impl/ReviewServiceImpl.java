package com.example.jingdongdemo.service.impl;

import cn.hutool.json.JSONUtil;
import com.example.jingdongdemo.dto.ReviewRequest;
import com.example.jingdongdemo.entity.Order;
import com.example.jingdongdemo.entity.Review;
import com.example.jingdongdemo.mapper.OrderMapper;
import com.example.jingdongdemo.mapper.ReviewMapper;
import com.example.jingdongdemo.service.ReviewService;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.ReviewVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

// service/impl/ReviewServiceImpl.java
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;

    @Override
    public void addReview(ReviewRequest req) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 校验订单已完成且属于当前用户
        Order order = orderMapper.getByNo(req.getOrderNo());
        if (order == null || !order.getUserId().equals(userId)) throw new RuntimeException("订单无效");
        if (order.getStatus() != 3) throw new RuntimeException("仅已完成订单可评价");

        String images = JSONUtil.toJsonStr(req.getImages());

        Review review = Review.builder()
                .orderId(order.getId()).orderNo(req.getOrderNo())
                .productId(req.getProductId()).userId(userId)
                .rating(req.getRating()).content(req.getContent())
                .images(images).isAnonymous(req.getIsAnonymous()).build();
        reviewMapper.insert(review);
    }

    @Override
    public PageResultVO<ReviewVO> productReviews(Long productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ReviewVO> list = reviewMapper.listByProduct(productId);
        PageInfo<ReviewVO> info = new PageInfo<>(list);

        // 匿名处理
        for (ReviewVO r : list) {
            if (r.getIsAnonymous() != null && r.getIsAnonymous() == 1) {
                r.setNickname("匿名用户");
                r.setAvatar(null);
            }
        }

        PageResultVO<ReviewVO> result = new PageResultVO<>();
        result.setList(list);
        result.setTotal(info.getTotal());
        result.setPageNum(pageNum); result.setPageSize(pageSize); result.setPages(info.getPages());
        return result;
    }
}