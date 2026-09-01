package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * B端 - 文件上传
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/v1")
public class UploadController {

    private final MinioService minioService;

    /** 上传商品图片，返回可访问 URL */
    @PostMapping("/upload")
    public R<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        return R.ok(Map.of("url", minioService.uploadImage(file)));
    }
}