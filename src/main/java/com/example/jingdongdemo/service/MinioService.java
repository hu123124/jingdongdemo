package com.example.jingdongdemo.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

    /** 上传图片，返回可访问的 URL */
    String uploadImage(MultipartFile file);
}