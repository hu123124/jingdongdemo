package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.entity.User;
import com.example.jingdongdemo.mapper.UserMapper;
import com.example.jingdongdemo.vo.PageResultVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * B端 - 用户管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserMapper userMapper;

    /** B端 - 用户列表 */
    @GetMapping
    public R<PageResultVO<Map<String,Object>>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll();
        PageInfo<User> info = new PageInfo<>(list);
        List<Map<String,Object>> voList = new java.util.ArrayList<>();
        for (User u : list) {
            Map<String,Object> m = new HashMap<>();
            m.put("id", u.getId()); m.put("username", u.getUsername());
            m.put("phone", u.getPhone()); m.put("status", u.getStatus());
            m.put("createTime", u.getCreateTime() != null ? u.getCreateTime().toString() : null);
            m.put("lastLoginTime", u.getLastLoginTime() != null ? u.getLastLoginTime().toString() : null);
            voList.add(m);
        }
        PageResultVO<Map<String,Object>> result = new PageResultVO<>();
        result.setList(voList); result.setTotal(info.getTotal());
        result.setPageNum(pageNum); result.setPageSize(pageSize); result.setPages(info.getPages());
        return R.ok(result);
    }

    /** B端 - 用户详情 */
    @GetMapping("/{id}")
    public R<User> detail(@PathVariable Long id) {
        return R.ok(userMapper.getByUserId(id));
    }

    /** B端 - 启用/禁用用户 */
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String,Integer> body) {
        userMapper.updateStatus(id, body.get("status"));
        return R.ok();
    }
}
