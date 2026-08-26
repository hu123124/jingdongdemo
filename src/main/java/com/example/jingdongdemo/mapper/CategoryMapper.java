package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.entity.Address;
import com.example.jingdongdemo.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("select * from t_category where status = 1 order by sort")
    List<Category> getAllCategory();

    /** B端 - 新增分类 */
    @Insert("INSERT INTO t_category (parent_id, name, icon, sort, status, create_time, update_time) VALUES (#{parentId}, #{name}, #{icon}, #{sort}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    /** B端 - 修改分类 */
    @Update("UPDATE t_category SET parent_id=#{parentId}, name=#{name}, icon=#{icon}, sort=#{sort}, update_time=NOW() WHERE id=#{id}")
    void update(Category category);

    /** B端 - 删除分类 */
    @Delete("DELETE FROM t_category WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
