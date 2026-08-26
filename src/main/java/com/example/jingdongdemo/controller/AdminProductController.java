package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.entity.Product;
import com.example.jingdongdemo.mapper.ProductMapper;
import com.example.jingdongdemo.service.ProductService;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.ProductVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * B端 - 商品管理
 */
@RestController @RequiredArgsConstructor
@RequestMapping("/api/admin/v1/products")
public class AdminProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    /** B端 - 商品列表（含下架商品） */
    @GetMapping
    public R<PageResultVO<Product>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "15") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> list = productMapper.listAllAdmin();
        PageInfo<Product> info = new PageInfo<>(list);
        PageResultVO<Product> result = new PageResultVO<>();
        result.setList(list); result.setTotal(info.getTotal());
        result.setPageNum(pageNum); result.setPageSize(pageSize); result.setPages(info.getPages());
        return R.ok(result);
    }

    /** B端 - 上下架 */
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        productService.updateStatus(id, body.get("status"));
        return R.ok();
    }

    /** B端 - 修改商品 */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        productMapper.updateProduct(id,
                (String) body.get("name"),
                (String) body.get("subtitle"),
                new java.math.BigDecimal(body.getOrDefault("price","0").toString()),
                (String) body.get("mainImage"),
                (String) body.get("detail"));
        return R.ok();
    }

    /** B端 - 新增商品 */
    @PostMapping
    public R<Map<String,Long>> create(@RequestBody Map<String, Object> body) {
        Product p = new Product();
        p.setCategoryId(Long.valueOf(body.get("categoryId").toString()));
        p.setName((String) body.get("name"));
        p.setSubtitle((String) body.getOrDefault("subtitle", null));
        p.setMainImage((String) body.getOrDefault("mainImage", null));
        p.setDetail((String) body.getOrDefault("detail", null));
        p.setPrice(new BigDecimal(body.getOrDefault("price", "0").toString()));
        p.setStock(0);
        p.setStatus(1);
        p.setSales(0);
        productMapper.insertProduct(p);
        return R.ok(Map.of("id", p.getId()));
    }
}
