package com.leeup.common;

/**
 * @ClassName Const
 * @Description TODO
 * @Author 李闯
 * @Date 2018/8/11 21:44
 * @Version 1.0
 **/
public class Const {

    public final static String CURRENT_USER = "currentUser";//User的常量

    public final static String EMAIL = "email";//Email的常量
    public final static String USERNAME = "username";//username的常量

    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }
}
