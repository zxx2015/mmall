package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;

import java.util.List;

/**
 * Create by zhouxin
 **/

public interface IProductService {

    ServerResponse<String> saveProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> getDetail(Integer productId);

    ServerResponse<PageInfo> getList(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> search(String productName,Integer productId,int pageNum, int pageSize);

    ServerResponse<PageInfo> getListByCategoryId(String keyword,Integer categoryId,int pageNum, int pageSize,String orderBy);

    ServerResponse<ProductDetailVo> getDetailPortal(Integer productId);

}
