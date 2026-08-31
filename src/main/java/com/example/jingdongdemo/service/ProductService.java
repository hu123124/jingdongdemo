package com.example.jingdongdemo.service;

import cn.hutool.db.PageResult;
import com.example.jingdongdemo.dto.ProductPageRequest;
import com.example.jingdongdemo.entity.Product;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.ProductSPUVO;
import com.example.jingdongdemo.vo.ProductVO;

import java.util.List;
import java.util.Map;

public interface ProductService {


    /**
     * 查商品详情
     */

    PageResultVO<ProductVO> listProduct(ProductPageRequest productPageRequest);

    ProductSPUVO getDetailById(Long id);

    List<ProductVO> recommend(Integer limit);

    /**
     * admin
     * @param id
     * @param status
     */
    // ProductService 加
    void updateStatus(Long id, Integer status);

    // ==================== B端 ====================

    /**
     * B端 - 商品列表（含下架商品，分页）
     */
    PageResultVO<Product> adminList(Integer pageNum, Integer pageSize);

    /**
     * B端 - 修改商品
     */
    void adminUpdate(Long id, Map<String, Object> body);

    /**
     * B端 - 新增商品
     * @return 新商品 id
     */
    Long adminCreate(Map<String, Object> body);
}
