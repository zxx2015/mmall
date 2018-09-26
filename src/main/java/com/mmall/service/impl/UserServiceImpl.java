package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 *
 */

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String userName, String password) {
        int resultCount = userMapper.checkUserName(userName);
        if (resultCount == 0) return ServerResponse.createByErrorMessage("用户名不存在");

        String md5password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(userName, md5password);
        if (user == null) return ServerResponse.createByErrorMessage("密码错误");

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);


    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse serverResponse = checkValid(user.getUsername(),Const.USERNAME);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }

        serverResponse = checkValid(user.getEmail(),Const.EMAIL);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (!StringUtils.isBlank(type)) {
            int resultCount;
            if (Const.USERNAME.equals(type)) {
                resultCount = userMapper.checkUserName(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户已存在");
                }
            }

            if (Const.EMAIL.equals(type)) {
                resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已被注册");
                }
            }
        } else return ServerResponse.createByErrorMessage("参数错误");

        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> getForgetQuestion(String username){
        ServerResponse serverResponse = checkValid(username,Const.USERNAME);
        if(serverResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String question = userMapper.getForgetQuestion(username);
        if(StringUtils.isBlank(question)){
            return ServerResponse.createByErrorMessage("找回密码的问题为空");
        }
        return ServerResponse.createBySuccess(question);
    }

    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){

        int resultCount = userMapper.forgetCheckAnswer(username,question,answer);
        if(resultCount==0) {
            return ServerResponse.createByErrorMessage("问题答案错误");
        }
        //设置token
        String forgetToken = UUID.randomUUID().toString();
        TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
        return ServerResponse.createBySuccess(forgetToken);
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        ServerResponse serverResponse = checkValid(username,Const.USERNAME);
        if(serverResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误或token过期");
        }

        if(StringUtils.equals(forgetToken,TokenCache.getKey(TokenCache.TOKEN_PREFIX+username))){
            int resultCount;
            String md5PasswordNew = MD5Util.MD5EncodeUtf8(passwordNew);
            resultCount=userMapper.updateByUsername(username, md5PasswordNew);

            if(resultCount>0){
               return ServerResponse.createBySuccessMessage("修改密码成功");
            }
            else {
                return ServerResponse.createByErrorMessage("操作失败");
            }
        }
        else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取token");
        }


    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user){
        //防止横向越权:加入userid的校验
        int resultCount = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));
        if(resultCount>0){
            String md5NewPassword = MD5Util.MD5EncodeUtf8(passwordNew);
            resultCount=userMapper.updateByUsername(user.getUsername(),md5NewPassword);
            if(resultCount>0){
                return ServerResponse.createBySuccessMessage("重置密码成功");
            }
            else {
                return ServerResponse.createByErrorMessage("操作失败");
            }
        }

        return ServerResponse.createByErrorMessage("旧密码输入错误");

    }

    public ServerResponse<User> updateInformation(User user){
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("邮箱已被其他用户注册");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        resultCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(resultCount>0){
            return ServerResponse.createBySuccess("信息更新成功",updateUser);
        }
        else return ServerResponse.createByErrorMessage("操作失败");
    }


    public ServerResponse<User> getInformation(int userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse<String > checkAdminRole(User user){

        if(user!=null&&user.getRole().intValue()==Const.Role.ROLE_ADDMIN){
            return ServerResponse.createBySuccess();
        }

        return ServerResponse.createByError();
    }

}