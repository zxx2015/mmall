package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import java.util.*;

import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.iterator;

/**
 * Create by zhouxin
 **/
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public ServerResponse<String> addCategory(Integer parentId, String categoryName){
        if(parentId == null||StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        //构造category对象并插入分类表
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int rowCount = categoryMapper.insert(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("添加分类成功");
        }
        return ServerResponse.createByErrorMessage("添加分类失败");
    }

    public ServerResponse<String> setCategoryName(Integer categoryId,String categoryName){
        if(categoryId== null||StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int result=categoryMapper.updateByPrimaryKeySelective(category);
        if(result>0){
            return ServerResponse.createBySuccessMessage("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    public ServerResponse<List<Category>> getCategory(Integer categoryId){
        if(categoryId==null) return ServerResponse.createByErrorMessage("参数错误");
//        Category category = categoryMapper.selectByPrimaryKey(categoryId);
//        if(category==null){
//            return ServerResponse.createByErrorMessage("未找到该品类");
//        }
        //寻找子类
        List<Category> list=categoryMapper.getCategory(categoryId);
        if(CollectionUtils.isEmpty(list)){

            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(list);
    }

    //每次从list中拿出一个id寻找它的子节点，并把它的子节点加入集合，直到集合为空，递归结束。
    public ServerResponse<Set<Integer>> getDeepCategoryId(Integer categoryId){

        if(categoryId==null){
            return ServerResponse.createByErrorMessage("参数错误");
        }

        List<Integer> list = new ArrayList<>();
        list.add(categoryId);

        ServerResponse<List<Category>> response = getCategory(categoryId);
            List<Category> tmpList = response.getData();
            for (Category category : tmpList) {
                list.add(category.getId());
            }

            for (int i = 0;i<list.size();i++) {
                tmpList = getCategory(list.get(i)).getData();
                for (Category category : tmpList) {
                    list.add(category.getId());
                }
            }
            Set<Integer> set = new HashSet<>();

            //去重
        for (int i:list
             ) {
            set.add(i);
        }
        return ServerResponse.createBySuccess(set);

    }





}
