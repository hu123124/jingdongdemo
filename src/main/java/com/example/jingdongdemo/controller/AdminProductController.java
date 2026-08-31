package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.entity.Product;
import com.example.jingdongdemo.service.ProductService;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.ProductSPUVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * B端 - 商品管理
 */
@RestController @RequiredArgsConstructor
@RequestMapping("/api/admin/v1/products")
public class AdminProductController {
    private final ProductService productService;

    /** B端 - 商品列表（含下架商品） */
    @GetMapping
    public R<PageResultVO<Product>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "15") Integer pageSize) {
        return R.ok(productService.adminList(pageNum, pageSize));
    }

    /** B端 - 商品详情（含 SKU 列表，编辑时回填用） */
    @GetMapping("/{id}")
    public R<ProductSPUVO> detail(@PathVariable Long id) {
        return R.ok(productService.getDetailById(id));
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
        productService.adminUpdate(id, body);
        return R.ok();
    }

    /** B端 - 新增商品 */
    @PostMapping
    public R<Map<String, Long>> create(@RequestBody Map<String, Object> body) {
        return R.ok(Map.of("id", productService.adminCreate(body)));
    }
}
