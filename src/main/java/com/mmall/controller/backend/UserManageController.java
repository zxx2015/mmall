package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Create by zhouxin
 **/
@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session){

       ServerResponse<User> response = iUserService.login(username, password);
       if(response.isSuccess()){
           if(response.getData().getRole()==Const.Role.ROLE_ADDMIN){
               session.setAttribute(Const.CURRENT_USER,response.getData());
               return response;
           }
           else {
               return ServerResponse.createByErrorMessage("用户不是管理员，无法登录后台");
           }
       }
       return response;
    }

}
