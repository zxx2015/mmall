package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import com.mmall.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Create by zhouxin
 **/
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse<Map> addShipping(Shipping shipping) {
        if (shipping != null) {
            shipping.setCreateTime(new Date());
            shipping.setUpdateTime(new Date());
            //传入的参数没有主键应该怎么办呢？
            int result = shippingMapper.insertSelective(shipping);
            if (result > 0) {
                Map map = Maps.newHashMap();
                map.put("shippingId", shipping.getId());
                return ServerResponse.createBySuccess("新建地址成功", map);
            }
            return ServerResponse.createByErrorMessage("新建地址失败");
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(), "传入的参数错误");
    }

    public ServerResponse<String> deleteShipping(Integer shippingId) {
        if (shippingId != null) {
            int count = shippingMapper.deleteByPrimaryKey(shippingId);
            if (count > 0) {
                return ServerResponse.createBySuccessMessage("删除地址成功");
            }
            return ServerResponse.createByErrorMessage("删除地址失败");
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(), "传入的参数为空");
    }

    public ServerResponse<String> updateShipping(Shipping shipping,Integer userId) {
        if (shipping != null) {
            shipping.setUserId(userId);
            int count = shippingMapper.updateByPrimaryKey(shipping);
            if (count > 0) {
                return ServerResponse.createBySuccessMessage("更新地址成功");
            }
            return ServerResponse.createByErrorMessage("更新地址失败");
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(), "传入的参数错误");
    }

    public ServerResponse<Shipping> selectShipping(Integer shippingId) {

        //如果shippingId为null的话，数据库不会报错，只会返回null，直接返回给前端null即可，不用报错。
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);

        return ServerResponse.createBySuccess(shipping);

    }


    public ServerResponse<PageInfo> getList(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippings);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
