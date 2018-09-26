package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param(value = "userId") Integer userId,@Param(value = "productId") Integer productId);

    List<Cart> selectByUserId(Integer userId);

    int selectByUserIdAndChecked(Integer userId);

    int deleteByUserIdAndProductId(@Param(value = "userId") Integer userId,@Param(value = "productIds")List productIds);

    int updateByUserIdAndChecked(@Param(value = "userId") Integer userId,@Param(value = "productId") Integer productId,@Param(value = "checked") Integer checked);
}