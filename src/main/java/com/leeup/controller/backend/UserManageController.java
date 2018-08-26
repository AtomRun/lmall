package com.leeup.controller.backend;

import com.leeup.common.Const;
import com.leeup.common.ServerResponse;
import com.leeup.pojo.User;
import com.leeup.service.IUserService;
import net.sf.jsqlparser.schema.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @ClassName UserManageController
 * @Description 后台管理员相关功能
 * @Author李闯
 * @Date 2018/8/25 18:24
 * @Version 1.0
 **/
@Controller
@RequestMapping("/manager/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    //后台管理员登录的功能
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){

        //直接调用前台用户的service登录方法
        ServerResponse<User> response = iUserService.login(username,password);
        //对response进行判断
        if (response.isSuccess()){
            //请求成功，获取这个user,从泛型的数据里拿到这个user
            User user = response.getData();
            //判断用户类型
            if (user.getRole()== Const.Role.ROLE_ADMIN){
                //类型为管理员，把用户放到session中
                session.setAttribute(Const.CURRENT_USER,user);
                //是管理员就将登录成功的数据信息返回给前台
                return response;
            }else {
                return ServerResponse.createByErrorMessage("您不是管理员，无法登录");
            }
        }
        //如果response.isSuccess未执行，也就是说不是成功的，直接返回response
        return response;
    }
}
