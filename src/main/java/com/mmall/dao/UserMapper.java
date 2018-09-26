package com.mmall.dao;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);

    int checkEmail(String email);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    String getForgetQuestion(String username);

    int  forgetCheckAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updateByUsername(@Param("username") String username,@Param("passwordNew") String passwordNew);

    int checkPassword(@Param("userId") int userId,@Param("password") String password);

    int checkEmailByUserId(@Param("email") String email,@Param("userId") int userId);
}