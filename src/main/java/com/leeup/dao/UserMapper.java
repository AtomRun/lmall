package com.leeup.dao;

import com.leeup.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //检查用户名的方法
    int checkUsername(String username);

    //检查邮件是否重复的方法
    int checkEmail(String email);

    //检查用户名和密码是否匹配
    User selectLogin(@Param("username") String username, @Param("password") String password);

    //查询用户的重置密码的问题
    String selectQuestionByUser(String username);

    //检查忘记密码时填写的问题答案是否正确
    int checkAnswer(@Param("username") String username,@Param("question")String question,@Param("answer")String answer);

    //根据用户名更新密码
    int updatePasswordByUsername(@Param("username") String username,@Param("passwordNew") String passwordNew);

    //检查用户的旧密码是不是这个用户的，防止横向越权
    int checkPassword(@Param("password")String password,@Param("userId")Integer userId);

    //根据userId查询当前库里有没有这个email，并且不能查当前用户的email
    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);

}