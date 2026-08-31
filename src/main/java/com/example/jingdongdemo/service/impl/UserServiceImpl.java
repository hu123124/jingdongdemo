package com.example.jingdongdemo.service.impl;



import com.example.jingdongdemo.dto.LoginRequest;
import com.example.jingdongdemo.dto.PasswordUpdateRequest;
import com.example.jingdongdemo.dto.UpdateProfileRequest;
import com.example.jingdongdemo.entity.User;
import com.example.jingdongdemo.mapper.UserMapper;
import com.example.jingdongdemo.service.AuthService;
import com.example.jingdongdemo.service.UserService;
import com.example.jingdongdemo.vo.LoginResultVO;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.UserInfoVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserInfoVO info() {
        Long userId =(Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.getByUserId(userId);

        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setGender(user.getGender());
        vo.setCreateTime(user.getCreateTime());
        //substring(0,3)0开始，数3个
        vo.setPhone(user.getPhone().substring(0,3)+"****"+user.getPhone().substring(7));
        return vo;
    }

    @Override
    public void updateInfo(UpdateProfileRequest updateProfileRequest) {
        User user =new User();
        user.setId(((Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        user.setGender(updateProfileRequest.getGender());
        user.setNickname(updateProfileRequest.getNickname());
        user.setEmail(updateProfileRequest.getEmail());
        user.setAvatar(updateProfileRequest.getAvatar());
        userMapper.updateInfo(user);
    }

    @Override
    public void changePassword(PasswordUpdateRequest passwordUpdateRequest) {
        Long userId =(Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.getByUserId(userId);
        if(passwordEncoder.matches(passwordUpdateRequest.getOldPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
            userMapper.updateInfo(user);
        }
    }

    // ==================== B端 ====================

    /**
     * B端 - 用户列表（分页，只返回需要展示的字段）
     */
    @Override
    public PageResultVO<Map<String, Object>> adminList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll();
        PageInfo<User> info = new PageInfo<>(list);
        List<Map<String, Object>> voList = new java.util.ArrayList<>();
        for (User u : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId()); m.put("username", u.getUsername());
            m.put("phone", u.getPhone()); m.put("status", u.getStatus());
            m.put("createTime", u.getCreateTime() != null ? u.getCreateTime().toString() : null);
            m.put("lastLoginTime", u.getLastLoginTime() != null ? u.getLastLoginTime().toString() : null);
            voList.add(m);
        }
        PageResultVO<Map<String, Object>> result = new PageResultVO<>();
        result.setList(voList); result.setTotal(info.getTotal());
        result.setPageNum(pageNum); result.setPageSize(pageSize); result.setPages(info.getPages());
        return result;
    }

    /**
     * B端 - 用户详情
     */
    @Override
    public User adminDetail(Long id) {
        return userMapper.getByUserId(id);
    }

    /**
     * B端 - 启用/禁用用户
     */
    @Override
    public void adminUpdateStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }
}
