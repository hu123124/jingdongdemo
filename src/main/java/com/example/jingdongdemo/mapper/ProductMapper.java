package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.dto.ProductPageRequest;
import com.example.jingdongdemo.entity.OrderItem;
import com.example.jingdongdemo.entity.Product;
import com.example.jingdongdemo.entity.ProductSKU;
import com.example.jingdongdemo.vo.ProductSKUVO;
import com.example.jingdongdemo.vo.ProductSPUVO;
import com.example.jingdongdemo.vo.ProductVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品 Mapper —— 操作 t_product（SPU）/ t_product_sku（SKU）表
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

    // ==================== B端 ====================

    /** B端 - 上下架 */
    @Update("UPDATE t_product SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** B端 - 商品列表（含下架，价格取 SKU 最低价） */
    @Select("SELECT p.*, COALESCE((SELECT MIN(s.price) FROM t_product_sku s WHERE s.product_id = p.id), 0) AS price " +
            "FROM t_product p ORDER BY p.create_time DESC")
    List<Product> listAllAdmin();

    /** B端 - 新增商品（SPU，价格在 SKU 上维护） */
    @Insert("INSERT INTO t_product (category_id, name, subtitle, main_image, detail, stock, status, sales, create_time, update_time) " +
            "VALUES (#{categoryId}, #{name}, #{subtitle}, #{mainImage}, #{detail}, #{stock}, #{status}, #{sales}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertProduct(Product product);

    /** B端 - 修改商品（SPU 信息，不含价格） */
    @Update("UPDATE t_product SET name=#{name}, subtitle=#{subtitle}, main_image=#{mainImage}, detail=#{detail}, update_time=NOW() WHERE id=#{id}")
    void updateProduct(@Param("id") Long id, @Param("name") String name, @Param("subtitle") String subtitle,
                       @Param("mainImage") String mainImage, @Param("detail") String detail);

    // ==================== SKU ====================

    /** B端 - 新增 SKU */
    @Insert("INSERT INTO t_product_sku (product_id, sku_code, spec, price, stock, status, create_time, update_time) " +
            "VALUES (#{productId}, #{skuCode}, #{spec}, #{price}, #{stock}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertSku(ProductSKU sku);

    /** B端 - 修改 SKU */
    @Update("UPDATE t_product_sku SET spec=#{spec}, price=#{price}, stock=#{stock}, update_time=NOW() WHERE id=#{id}")
    void updateSku(ProductSKU sku);

    /** B端 - 删除商品下所有 SKU */
    @Delete("DELETE FROM t_product_sku WHERE product_id = #{productId}")
    void deleteSkusByProductId(@Param("productId") Long productId);

    /** B端 - 删除商品下不在指定 id 集合中的 SKU（更新时清理被移除的规格） */
    void deleteSkusNotIn(@Param("productId") Long productId, @Param("keepIds") List<Long> keepIds);
}
