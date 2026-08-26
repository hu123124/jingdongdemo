package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    @Select("SELECT * FROM t_admin WHERE username = #{username}")
    Admin getByUsername(String username);

    @Select("SELECT * FROM t_admin WHERE id = #{id}")
    Admin getById(Long id);
}
