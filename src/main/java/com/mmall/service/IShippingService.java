package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

import java.util.Map;

/**
 * Create by zhouxin
 **/
public interface IShippingService {

    ServerResponse<Map> addShipping(Shipping shipping);

    ServerResponse<String> deleteShipping(Integer shippingId);

    ServerResponse<String> updateShipping(Shipping shipping,Integer userId);

    ServerResponse<Shipping> selectShipping(Integer shippingId);

    ServerResponse<PageInfo> getList(Integer pageNum, Integer pageSize, Integer userId);
}
