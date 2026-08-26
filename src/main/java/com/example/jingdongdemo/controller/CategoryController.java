package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.dto.AddressRequest;
import com.example.jingdongdemo.service.AddressService;
import com.example.jingdongdemo.service.CategoryService;
import com.example.jingdongdemo.vo.AddressVO;
import com.example.jingdongdemo.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public R<List<CategoryVO>> getCategories(){
        return R.ok(categoryService.getCategory());
    }
}
