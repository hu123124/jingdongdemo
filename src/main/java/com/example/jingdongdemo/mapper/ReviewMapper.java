package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.entity.Review;
import com.example.jingdongdemo.vo.ReviewVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

// mapper/ReviewMapper.java
@Mapper
public interface ReviewMapper {

    @Insert("INSERT INTO t_review (order_id, order_no, product_id, user_id, rating, content, images, is_anonymous, create_time) " +
            "VALUES (#{orderId}, #{orderNo}, #{productId}, #{userId}, #{rating}, #{content}, " +
            "#{images,jdbcType=VARCHAR}, #{isAnonymous}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Review review);

    @Select("SELECT r.*, u.nickname, u.avatar FROM t_review r " +
            "LEFT JOIN t_user u ON r.user_id = u.id " +
            "WHERE r.product_id = #{productId} ORDER BY r.create_time DESC")
    List<ReviewVO> listByProduct(@Param("productId") Long productId);
}