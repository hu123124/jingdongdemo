package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.entity.Category;
import com.example.jingdongdemo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * B端 - 分类管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/v1/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    /** B端 - 分类列表 */
    @GetMapping
    public R<List<Category>> list() {
        return R.ok(categoryService.adminList());
    }

    /** B端 - 新增分类 */
    @PostMapping
    public R<Void> create(@RequestBody Map<String, Object> body) {
        categoryService.adminCreate(body);
        return R.ok();
    }

    /** B端 - 修改分类 */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        categoryService.adminUpdate(id, body);
        return R.ok();
    }

    /** B端 - 删除分类 */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        categoryService.adminDelete(id);
        return R.ok();
    }
}
