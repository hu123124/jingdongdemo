package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.entity.Order;
import com.example.jingdongdemo.entity.Payment;
import com.example.jingdongdemo.vo.OrderConfirmAddressVO;
import com.example.jingdongdemo.vo.OrderConfirmItemVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PaymentMapper {

    @Insert("insert into t_payment ( order_no, user_id, pay_no, pay_channel, pay_amount, status, pay_time,create_time,update_time)" +
            "values (#{orderNo},#{userId},#{payNo},#{payChannel},#{payAmount},#{status},#{payTime},NOW(),NOW())")
    void insert(Payment payment);

    @Select("select * from t_payment where pay_no = #{payNo}")
    Payment getByPayNo(String payNo);
}
