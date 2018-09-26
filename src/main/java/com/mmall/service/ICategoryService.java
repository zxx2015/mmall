package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;
import java.util.Set;

/**
 * Create by zhouxin
 **/
public interface ICategoryService {
    ServerResponse<String> addCategory(Integer parentId, String categoryName);

    ServerResponse<String> setCategoryName(Integer categoryId,String categoryName);

    ServerResponse<List<Category>> getCategory(Integer categoryId);

    ServerResponse<Set<Integer>> getDeepCategoryId(Integer categoryId);
}
