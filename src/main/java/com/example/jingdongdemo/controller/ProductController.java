package com.example.jingdongdemo.controller;


import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.dto.ProductPageRequest;
import com.example.jingdongdemo.entity.Product;
import com.example.jingdongdemo.service.ProductService;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.ProductSPUVO;
import com.example.jingdongdemo.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 商品列表（分页）
     * 浏览器访问: http://localhost:8080/api/v1/products?pageNum=1&pageSize=5
     */
    @GetMapping
    public R<PageResultVO<ProductVO>> listProduct(ProductPageRequest productPageRequest) {
        return R.ok(productService.listProduct(productPageRequest));
    }


    @GetMapping("/{id}")
    public R<ProductSPUVO> detail(@PathVariable Long id) {
        return R.ok(productService.getDetailById(id));
    }

    @GetMapping("/recommend")
    public R<List<ProductVO>> recommend(@RequestParam(defaultValue = "10") Integer limit) {
        return R.ok(productService.recommend(limit));
    }

}
