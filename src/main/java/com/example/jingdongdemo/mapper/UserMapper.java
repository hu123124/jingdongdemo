package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    void insert(User user);

    @Select("select * from t_user where username =#{username}")
    User getByUsername(@Param("username") String username);

    @Select("select * from t_user where id = #{id}")
    User getByUserId(Long id);

    void updateInfo(User user);

    /** B端 - 用户列表 */
    List<User> selectAll();

    /** B端 - 启用/禁用 */
    @Update("UPDATE t_user SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 记录登录时间 */
    @Update("UPDATE t_user SET last_login_time = NOW() WHERE id = #{id}")
    void updateLoginTime(@Param("id") Long id);
}
