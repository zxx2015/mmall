package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Create by zhouxin
 **/
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getListByCategoryId(@RequestParam(value = "keyword",required = false) String keyword, @RequestParam(value = "categoryId",required = false)Integer categoryId,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize, String orderBy){
        if(categoryId==null||StringUtils.isBlank(orderBy)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return iProductService.getListByCategoryId(keyword,categoryId,pageNum,pageSize,orderBy);

    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetailById(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return iProductService.getDetailPortal(productId);
    }
}
