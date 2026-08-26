package com.example.jingdongdemo.service.impl;




import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.example.jingdongdemo.entity.Category;
import com.example.jingdongdemo.mapper.CategoryMapper;
import com.example.jingdongdemo.service.CategoryService;
import com.example.jingdongdemo.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<CategoryVO> getCategory() {
        // 1. 先查缓存
        String cacheKey = "categories:tree";
        List<CategoryVO> cached = (List<CategoryVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("商品分类命中缓存");
            return cached;
        }
        log.info("商品分类未命中缓存");
        List<Category> all = categoryMapper.getAllCategory();

        // 按 parentId 分组，同时转 VO
        Map<Long, List<CategoryVO>> childrenVOListWithParentId = new HashMap<>();
        List<CategoryVO> parentsVOList = new ArrayList<>();

        for (Category each : all) {
            CategoryVO eachVO = new CategoryVO();
            eachVO.setId(each.getId());
            eachVO.setName(each.getName());
            eachVO.setIcon(each.getIcon());

            childrenVOListWithParentId.computeIfAbsent(each.getParentId(), parentId -> new ArrayList<>())
                    .add(eachVO);
            if (each.getParentId() == 0) parentsVOList.add(eachVO);
        }

        // 递归挂子节点
        for (CategoryVO ParentVO : parentsVOList) {
            attachChildren(ParentVO, childrenVOListWithParentId);
        }

        // 3. 返回前写入缓存
        redisTemplate.opsForValue().set(cacheKey, parentsVOList, 30, TimeUnit.MINUTES);

        return parentsVOList;
    }

    private void attachChildren(CategoryVO ParentVO, Map<Long, List<CategoryVO>> childrenVOListWithParentId) {
        List<CategoryVO> childrenVOList = childrenVOListWithParentId.get(ParentVO.getId());
        if (childrenVOList != null) {
            ParentVO.setChildren(childrenVOList);
//            //为了考虑子层还要分类，所以遍历子节点集合调用？
//            for (CategoryVO childrenVO : childrenVOList) {
//                attachChildren(childrenVO, childrenVOListWithParentId);
//            }
        }
    }

    // ==================== B端 ====================

    @Override
    public List<Category> adminList() {
        return categoryMapper.getAllCategory();
    }

    @Override
    public void adminCreate(Map<String, Object> body) {
        Category c = new Category();
        c.setParentId(body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : 0L);
        c.setName((String) body.get("name"));
        c.setIcon((String) body.getOrDefault("icon", null));
        c.setSort(body.get("sort") != null ? Integer.valueOf(body.get("sort").toString()) : 0);
        c.setStatus(1);
        categoryMapper.insert(c);
        // 删除分类树缓存
        redisTemplate.delete("categories:tree");
    }

    @Override
    public void adminUpdate(Long id, Map<String, Object> body) {
        Category c = new Category();
        c.setId(id);
        c.setParentId(body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : 0L);
        c.setName((String) body.get("name"));
        c.setIcon((String) body.getOrDefault("icon", null));
        c.setSort(body.get("sort") != null ? Integer.valueOf(body.get("sort").toString()) : 0);
        categoryMapper.update(c);
        // 删除分类树缓存
        redisTemplate.delete("categories:tree");
    }

    @Override
    public void adminDelete(Long id) {
        categoryMapper.deleteById(id);
        // 删除分类树缓存
        redisTemplate.delete("categories:tree");
    }

}
