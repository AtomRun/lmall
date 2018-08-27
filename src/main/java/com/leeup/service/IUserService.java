package com.leeup.service;


import com.leeup.common.ServerResponse;
import com.leeup.pojo.User;
import org.springframework.stereotype.Service;

/**
 * @ClassName IUserService
 * @Description Service接口扩展
 * @Author 李闯
 * @Date 2018/8/11 20:30
 * @Version 1.0
 **/
@Service
public interface IUserService {

    //登录的方法
    ServerResponse<User> login(String username, String password);

    //注册的方法
    ServerResponse<String> register(User user);

    //检查用户名邮件的方法
    ServerResponse<String> checkValid(String str,String type);

    //找回密码的接口，检查找回密码的问题是否为空
    ServerResponse selectQuestion(String username);

    //检测问题的答案是否正确
    ServerResponse<String> checkAnswer(String username,String question,String answer);

    //忘记密码-重置密码
    ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken);

    //登录状态重置密码
    ServerResponse<String> resetPassword(String passwordOld,String passwordNew, User user);

    //更新个人信息的接口
    ServerResponse<User> updateInformation(User user);

    //获取个人信息
    ServerResponse<User> getInformation(Integer userId);

    //检查当前用户是不是管理员
    ServerResponse checkAdminRole(User user);
}
