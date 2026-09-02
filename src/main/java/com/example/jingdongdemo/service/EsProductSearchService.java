package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.ProductPageRequest;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.ProductVO;

public interface EsProductSearchService {
    /** 关键词走 ES 全文检索（IK 分词 + 高亮 + 分页），返回与 MySQL 版相同结构 */
    PageResultVO<ProductVO> search(ProductPageRequest req);
}