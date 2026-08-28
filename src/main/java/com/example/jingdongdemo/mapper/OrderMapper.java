package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.dto.OrderRequest;
import com.example.jingdongdemo.entity.Address;
import com.example.jingdongdemo.entity.Order;
import com.example.jingdongdemo.vo.OrderConfirmAddressVO;
import com.example.jingdongdemo.vo.OrderConfirmAvailableCouponsVO;
import com.example.jingdongdemo.vo.OrderConfirmItemVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("select id,consignee," +
            "CONCAT(SUBSTRING(phone, 1, 3), '****', SUBSTRING(phone, 8, 4)) AS phone," +
            "concat(province,city,district,detail) as fullAddress " +
            "from t_address where user_id = #{userId} and is_default = 1")
    OrderConfirmAddressVO getDefaultAddress(Long userId);

    List<OrderConfirmItemVO> getItems(Long userId);

    void createOrder(Order order);

    List<Order> getOrderByUserIdAndStatus(@Param("userId") Long userId,@Param("status") Integer status);

    @Select("select * from t_order where order_no = #{orderNo}")
    Order getByNo(String orderNo);

    @Update("update t_order set status = 4,close_time = NOW(),update_time = NOW() where order_No = #{orderNo} and status = 0")
    void cancelOrder(String orderNo);

    @Update("update t_order set status = 3,receive_time = NOW(),update_time = NOW() where order_No = #{orderNo}")
    void receiveOrder(String orderNo);

    @Update("UPDATE t_order SET status = 1, pay_time = NOW(), update_time = NOW() WHERE order_no = #{orderNo}")
    void paySuccess(@Param("orderNo") String orderNo);

    List<Order> adminList(@Param("status") Integer status, @Param("orderNo") String orderNo);

    @Update("UPDATE t_order SET status = 2, ship_time = NOW(), update_time = NOW() WHERE order_no = #{orderNo}")
    void ship(@Param("orderNo") String orderNo);

    /** 查询超过 30 分钟未支付的订单 */
    @Select("SELECT * FROM t_order WHERE status = 0 AND create_time < DATE_SUB(NOW(), INTERVAL 30 MINUTE)")
    List<Order> findTimeoutUnpaidOrders();
}
