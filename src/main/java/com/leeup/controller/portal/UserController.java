package com.leeup.controller.portal;


import com.leeup.common.Const;
import com.leeup.common.ResponseCode;
import com.leeup.common.ServerResponse;
import com.leeup.pojo.User;
import com.leeup.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @ClassName UserController
 * @Description 用户登录类
 * @Author 李闯
 * @Date 2018/8/11 20:23
 * @Version 1.0
 **/
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * @Author 李闯
     * @Description： //用户登录
     * @Date： 20:35 2018/8/16
     * @Param： [username, password, session]
     * @return： java.lang.Object
     **/
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username,password);
        //只需要判断成功即可，因为Service的login方法已经判断了其他两种方式，如果出错在service就直接返回了
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    //检测用户名和Email是否存在，虽然我们在注册的时候已经调用了，但是为了防止直接访问接口的方式，所以再做一个检测
    //还要做到用户离开用户input输入框换到下一个输入框的时候有个实时的检测
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid (String str,String type){
        return iUserService.checkValid(str,type);
    }

    //获取登用户的信息的接口
    //使用session获取user
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
   public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user!=null){
            //如果从session获取到的user不为空，那么就是成功的,把user返回给前端由于user此时为Object 所以会调用createBySuccess的泛型data方法
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("该用户未登录！无法获取该用户当前登录信息！");
   }

   //忘记密码的接口,返回一个忘记密码的问题所以是String
   @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
   @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    //校验忘记密码的问题答案是否是正确的
    //返回的对象泛型为String,因为要把token放在里面
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    //上面的方法将token返回给前端之后，我们忘记密码重置密码时我们就需要这个token，我们拿到token时和缓存中的token做对比
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetRestPassword(username,passwordNew,forgetToken);
    }

    //登录状态的重置密码,因为要判断用户的登录状态所以需要HttpSession
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        //从session中获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    //更新 用户个人信息的接口
    //返回user是因为，更新完个人用户信息后，要把新的个人用户信息放到session中，同时要把session中的个人用户信息返回给前端，
    //前端可以直接更新到页面上
    //参数放user用来承载新的数据
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        //从session中获取当前用户
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        //只有在登录状态下才能更新用户的信息，user传过来的参数是email,答案，问题的提问，等等，是没有userId的
        //所以我们要把他的userId赋值成当前的userId
        user.setId(currentUser.getId());//防止越权问题，
        // 这样防止Id变化
        //username不能被更新的，所以我们把username也set上
        user.setUsername(currentUser.getUsername());

        ServerResponse response = iUserService.updateInformation(user);
        if (response.isSuccess()){
            //更新session
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    //获取用户详情信息，需要从Session中进行登录判断，如果调用这个接口没有进行登录，我们要进行一个强制登录
    //例如在个人用户信息修改信息的时候，我们要调用getInformation再调用updateInformation,因为我们在get的时候就可以判断使之强制登录，
    //在update时只用判断是否登录就可以了，正常返回status
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session){
        //从session中获取当前用户
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需强制登录");

        }
        //如果获取的用户不为空的话，把用户id传给service,service将密码置空，这样前台的用户可以看到他的个人信息。
        //包括他的修改，提问问题，提问问题的答案
        return iUserService.getInformation(currentUser.getId());
    }

}
