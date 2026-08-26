package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.dto.ProductPageRequest;
import com.example.jingdongdemo.entity.OrderItem;
import com.example.jingdongdemo.entity.Product;
import com.example.jingdongdemo.vo.ProductSKUVO;
import com.example.jingdongdemo.vo.ProductSPUVO;
import com.example.jingdongdemo.vo.ProductVO;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

/**
 * 商品 Mapper —— 直接操作 t_product 表
 */
@Mapper
public interface ProductMapper {


    List<Product> listProducts(ProductPageRequest productPageRequest);

    @Select("select * from t_product where id = #{id}")
    ProductSPUVO getProductById(Long id);

    @Select("select * from t_product_sku where product_id = #{productId}")
    List<ProductSKUVO> getProductSKUSBySPUId(Long productid);

    @Select("select * from t_product where status = 1 order by sales desc limit #{limit}")
    List<ProductVO> getRecommend(Integer limit);

    @Update("update t_product_sku set stock = stock+ #{quantity} where id = #{skuId}")
    void returnSku(OrderItem orderItem);

    /**
     * admin
     * @param id
     * @param status
     */
// ProductMapper 加
    @Update("UPDATE t_product SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** B端 - 商品列表（含下架） */
    @Select("SELECT * FROM t_product ORDER BY create_time DESC")
    List<Product> listAllAdmin();

    /** B端 - 新增商品 */
    @Insert("INSERT INTO t_product (category_id, name, subtitle, main_image, detail, price, stock, status, sales, create_time, update_time) " +
            "VALUES (#{categoryId}, #{name}, #{subtitle}, #{mainImage}, #{detail}, #{price}, #{stock}, #{status}, #{sales}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertProduct(Product product);

    /** B端 - 修改商品 */
    @Update("UPDATE t_product SET name=#{name}, subtitle=#{subtitle}, price=#{price}, main_image=#{mainImage}, detail=#{detail}, update_time=NOW() WHERE id=#{id}")
    void updateProduct(@Param("id") Long id, @Param("name") String name, @Param("subtitle") String subtitle,
                       @Param("price") java.math.BigDecimal price, @Param("mainImage") String mainImage,
                       @Param("detail") String detail);
}
