package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.dto.CartBatchDTO;
import com.example.jingdongdemo.entity.Address;
import com.example.jingdongdemo.entity.Cart;
import com.example.jingdongdemo.vo.CartVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    @Insert("insert into t_cart (user_id,product_id,sku_id,quantity,checked,create_time,update_time) values " +
            "(#{userId},#{productId},#{skuId},#{quantity},#{checked},NOW(),NOW())")
    void addCart(Cart cart);

    List<CartVO> getCarts(Long userId);

    @Update("update t_cart set quantity = #{quantity} where id = #{id}")
    void updateQuantityWithId(Long id, Integer quantity);

    @Update("update t_cart set checked = #{checked} where id = #{id}")
    void updateChecked(@Param("id") Long id,@Param("checked")Integer checked);

    void updateBatchChecked(@Param("checked") Integer checked, @Param("ids") List<Long> ids);

    @Delete("delete from t_cart where id = #{id}")
    void deleteCart(Long id);

    void batchDeleteCart(List<Long> ids);

    @Delete("delete from t_cart where user_id = #{userId} and checked = 1")
    void deleteChecked(Long userId);
}
