package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    //排序
    List<Product> selectList(@Param("what")String what,@Param("order")String order);

    List<Product> searchList(@Param("productName")String productName,@Param("productId") Integer productId);

    //查找并排序
    List<Product> searchListPortal(@Param("productName")String productName,@Param("categoryId") Integer categoryId,@Param("what")String what,@Param("order")String order);

    List<Product> searchByCategoryId(@Param("productName")String productName,@Param("categoryIds") List<Integer> categoryIds);
}