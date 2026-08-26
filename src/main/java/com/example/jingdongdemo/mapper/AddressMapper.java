package com.example.jingdongdemo.mapper;

import com.example.jingdongdemo.dto.AddressRequest;
import com.example.jingdongdemo.entity.Address;
import com.example.jingdongdemo.vo.AddressVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

    List<Address>  getByUserId(@Param("userId") Long userId);

@Select("select * from t_address where id=#{id}")
    Address getByAddressId(Long id);

    void buildAddress(Address address);

    @Update("update t_address set is_default = 0 where user_id = #{userId}")
    void setAllisDefaultByUserId(Long userId);

    void alterAddress(Address address);

    @Delete("delete from t_address where id = #{id}")
    void deleteAddress(Long id);

}
