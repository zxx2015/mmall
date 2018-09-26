package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;
import org.springframework.stereotype.Service;

/**
 * Create by zhouxin
 **/

public interface ICartService {
    ServerResponse<CartVo> add(Integer userId, Integer productId, int count);

    ServerResponse<CartVo> update(Integer userId,Integer productId, int count);

    ServerResponse<CartVo> delete(Integer userId,String productIds);

    ServerResponse<CartVo> getList(Integer userId);

    ServerResponse<CartVo> search(Integer productId,Integer userId);

    ServerResponse<CartVo> select(Integer productId,Integer userId);

    ServerResponse<CartVo> unSelect(Integer productId,Integer userId);

    ServerResponse<Integer> getCartProductCount(Integer userId);


    ServerResponse<CartVo> selectAllOrUnSelect(Integer userId,Integer productId,Integer checked);
}
