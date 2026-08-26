package com.example.jingdongdemo.service;

import com.example.jingdongdemo.entity.Category;
import com.example.jingdongdemo.vo.CategoryVO;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    /** C端 - 分类树 */
    List<CategoryVO> getCategory();

    /** B端 - 分类列表（全量） */
    List<Category> adminList();

    /** B端 - 新增分类 */
    void adminCreate(Map<String, Object> body);

    /** B端 - 修改分类 */
    void adminUpdate(Long id, Map<String, Object> body);

    /** B端 - 删除分类 */
    void adminDelete(Long id);
}