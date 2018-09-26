package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * Create by zhouxin
 **/
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;


    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(int parentId, String categoryName, HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要强制登录");
        }
        ServerResponse response = iUserService.checkAdminRole(currentUser);
        if(response.isSuccess()){
            //是管理员则添加分类信息
            return iCategoryService.addCategory(parentId,categoryName);
        }
        return ServerResponse.createByErrorMessage("无权限操作，完成该操作需要管理员权限");
    }

    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategoryName(int categoryId,String categoryName,HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要强制登录");
        }
        ServerResponse response = iUserService.checkAdminRole(currentUser);
        if(response.isSuccess()){
            return iCategoryService.setCategoryName(categoryId,categoryName);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作，完成该操作需要管理员权限");
        }
    }

    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getCategory(@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId, HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要强制登录");
        }
        ServerResponse response = iUserService.checkAdminRole(currentUser);
        if(response.isSuccess()){
            //寻找子类
            return iCategoryService.getCategory(categoryId);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作，完成该操作需要管理员权限");
        }
    }

    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse<Set<Integer>> getDeepCategory(Integer categoryId, HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要强制登录");
        }
        ServerResponse response = iUserService.checkAdminRole(currentUser);
        if(response.isSuccess()){
            return iCategoryService.getDeepCategoryId(categoryId);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作，完成该操作需要管理员权限");
        }
    }
}
