package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.entity.Category;
import com.example.jingdongdemo.entity.OrderItem;
import com.example.jingdongdemo.vo.OrderDetailItemVO;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Insert("insert into t_order_item  (order_id,order_no,product_id,sku_id,product_name,sku_spec,product_image,price,quantity,subtotal,create_time)" +
            "values (#{orderId},#{orderNo},#{productId},#{skuId},#{productName},#{skuSpec},#{productImage},#{price},#{quantity},#{subtotal},NOW())")
    void insert(OrderItem oi);

    // ✅ @Update + WHERE stock >= quantity + 返回值 = 影响行数
    @Update("UPDATE t_product_sku SET stock = stock - #{quantity} WHERE id = #{skuId} AND stock >= #{quantity}")
    int deductStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    @Select("select * from t_order_item where order_no = #{orderNo}")
    List<OrderItem> getByNo(String orderNo);


    @Select("select * from t_order_item where order_no = #{orderNo}")
    List<OrderItem> getReturnItem(String orderNo);
}
