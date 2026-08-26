package com.example.jingdongdemo.service;

import cn.hutool.db.PageResult;
import com.example.jingdongdemo.dto.ProductPageRequest;
import com.example.jingdongdemo.entity.Product;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.ProductSPUVO;
import com.example.jingdongdemo.vo.ProductVO;

import java.util.List;

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
}
