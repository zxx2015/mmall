package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.controller.backend.ProductManageController;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.TimeUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Create by zhouxin
 **/
@Service("iProductService")
public class ProduceServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse<String> saveProduct(Product product){
        //更改主图（mainPicture） +
        // 判断是更新还是增加----判断依据是product的id是否为null，
        if(product!=null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                //得到每张次图
                String[] pictures = product.getSubImages().split(",");
                if(pictures.length>0){
                    //设置第一张subimages为主图
                    product.setMainImage(pictures[0]);
                }
            }

            if(product.getId()!=null){
                int count = productMapper.updateByPrimaryKey(product);
                if(count > 0){
                    return ServerResponse.createBySuccessMessage("更新产品信息成功");
                }
            }
            else {
                int count = productMapper.insert(product);
                if(count > 0){
                    return ServerResponse.createBySuccessMessage("增加产品信息成功");
                }
            }
        }

        return ServerResponse.createByErrorMessage("更新产品失败");
    }

    public ServerResponse<String> setSaleStatus(Integer productId, Integer status){
        if(productId==null||status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),"参数错误");
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count>0){
            return ServerResponse.createBySuccessMessage("修改产品状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品状态失败");
    }

    public ServerResponse<ProductDetailVo> getDetail(Integer productId) {
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),"参数错误");
        }

        //构造data信息并返回
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product!=null){
            ProductDetailVo vo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(vo);

        }
        return ServerResponse.createByErrorMessage("获取商品详情失败");
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo vo = new ProductDetailVo();
        vo.setId(product.getId());
        vo.setCategoryId(product.getCategoryId());
        vo.setName(product.getName());
        vo.setSubtitle(product.getSubtitle());
        vo.setSubImages(product.getSubImages());
        vo.setMainImage(product.getMainImage());
        vo.setDetail(product.getDetail());
        vo.setPrice(product.getPrice());
        vo.setStatus(product.getStatus());
        vo.setStock(product.getStock());

        vo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category !=null) {
            vo.setParentCategoryId(category.getParentId());
        }
        else {
            vo.setParentCategoryId(0);
        }

        vo.setCreateTime(TimeUtil.timeToStr(product.getCreateTime()));
        vo.setUpdateTime(TimeUtil.timeToStr(product.getUpdateTime()));
        return vo;
    }

    public ServerResponse<PageInfo> getList(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.selectList("id","asc");
        List<ProductListVo> productListVosVos=new ArrayList<>();
        for (Product product:products) {
           productListVosVos.add(assembleProductListVo(product));
        }

        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVosVos);

        return ServerResponse.createBySuccess(pageInfo);

    }

    private ProductListVo assembleProductListVo(Product product){

        ProductListVo productListVo = new ProductListVo();
        product.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());

        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        return productListVo;

    }

    public ServerResponse<PageInfo> search(String productName,Integer productId,int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            //使用通配符%匹配字符串
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> products = productMapper.searchList(productName,productId);
        List<ProductListVo> productListVos = new ArrayList<>();
        for (Product p :
                products) {
            productListVos.add(assembleProductListVo(p));
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }



    public ServerResponse<PageInfo> getListByCategoryId(String keyword,Integer categoryId,int pageNum, int pageSize,String orderBy){
        //分类集合
        List<Integer> categorieIds = Lists.newArrayList();
        if(categoryId!=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category==null&&keyword==null){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> list = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(list);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categorieIds.addAll(iCategoryService.getDeepCategoryId(categoryId).getData());
        }

        //排序
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(orderBy)){
            String[] strings = orderBy.split("_");
            if(strings.length!=2){
                return ServerResponse.createByErrorMessage("参数错误");
            }
            PageHelper.orderBy(strings[0]+" "+strings[1]);
        }

        //在数据库中查找商品
        String productName = null;
        if(StringUtils.isNotBlank(keyword)){
            //使用通配符%匹配字符串
            productName = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        List<Product> products= productMapper.searchByCategoryId(productName,categorieIds.size()==0?null:categorieIds);

        List<ProductListVo> productListVos = new ArrayList<>();
        for (Product p :
                products) {
            productListVos.add(assembleProductListVo(p));
        }

        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);
        System.out.println(products);
        return ServerResponse.createBySuccess(pageInfo);

    }

    public ServerResponse<ProductDetailVo> getDetailPortal(Integer productId) {
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),"参数错误");
        }

        //构造data信息并返回
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product.getStatus()!=Const.ProductStatusEnums.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("商品已下架或者删除");
        }
        if(product!=null){
            ProductDetailVo vo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(vo);

        }
        return ServerResponse.createByErrorMessage("获取商品详情失败");
    }





}
