package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.entity.Coupon;
import com.example.jingdongdemo.entity.Order;
import com.example.jingdongdemo.entity.UserCoupon;
import com.example.jingdongdemo.vo.CouponVO;
import com.example.jingdongdemo.vo.OrderConfirmAddressVO;
import com.example.jingdongdemo.vo.OrderConfirmItemVO;
import com.example.jingdongdemo.vo.UserCouponVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CouponMapper {


    @Select("select * from t_coupon where status = 1 and NOW() between start_time and end_time and receive_count < total_count" +
            " and id not in (select coupon_id from t_user_coupon where user_id = #{userId})")
    List<CouponVO> getAvailableCoupon(Long userId);

    @Select("select * from t_coupon where status = 1 and NOW() between start_time and end_time and receive_count < total_count" +
            " and id not in (select coupon_id from t_user_coupon where user_id = #{userId})" +
            "and id= #{couponId}")
    Coupon getByCouponId(@Param("couponId") Long couponId,@Param("userId") Long userId);

    @Insert("insert into t_user_coupon (user_id,coupon_id,status,receive_time)" +
            "values (#{userId},#{couponId},#{status},NOW())")
    void receiveCoupon(UserCoupon userCoupon);
    // Mapper
    @Update("UPDATE t_coupon SET receive_count = receive_count + 1 WHERE id = #{couponId}")
    void incrReceiveCount(Long couponId);

    @Select("SELECT uc.id, uc.coupon_id, uc.status, uc.receive_time, " +
            "c.name, c.type, c.discount_value, c.min_amount " +
            "FROM t_user_coupon uc JOIN t_coupon c ON uc.coupon_id = c.id " +
            "WHERE uc.user_id = #{userId}")
    List<UserCouponVO> getMineCoupon(Long userId);

    /** B端 - 优惠券列表 */
    @Select("SELECT * FROM t_coupon ORDER BY create_time DESC")
    List<Coupon> selectAll();

    /** B端 - 根据ID查优惠券 */
    @Select("SELECT * FROM t_coupon WHERE id = #{id}")
    Coupon getByCouponId2(@Param("id") Long id);

    /** B端 - 新增优惠券 */
    @Insert("INSERT INTO t_coupon (name, type, discount_value, min_amount, total_count, receive_count, start_time, end_time, status, create_time, update_time) " +
            "VALUES (#{name}, #{type}, #{discountValue}, #{minAmount}, #{totalCount}, 0, #{startTime}, #{endTime}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCoupon(Coupon coupon);

    /** B端 - 修改优惠券 */
    @Update("UPDATE t_coupon SET name=#{name}, type=#{type}, discount_value=#{discountValue}, min_amount=#{minAmount}, " +
            "total_count=#{totalCount}, start_time=#{startTime}, end_time=#{endTime}, update_time=NOW() WHERE id=#{id}")
    void updateCoupon(Coupon coupon);

    /** B端 - 删除优惠券 */
    @Delete("DELETE FROM t_coupon WHERE id = #{id}")
    void deleteCoupon(@Param("id") Long id);

    /** B端 - 启用/禁用优惠券 */
    @Update("UPDATE t_coupon SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
