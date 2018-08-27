package com.leeup.service.impl;

import com.leeup.common.Const;
import com.leeup.common.ServerResponse;
import com.leeup.common.TokenCache;
import com.leeup.dao.UserMapper;
import com.leeup.pojo.User;
import com.leeup.service.IUserService;
import com.leeup.util.MD5Util;
import com.sun.corba.se.impl.oa.toa.TOA;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpSession;
import java.util.UUID;


/**
 * @ClassName UserServiceImpl
 * @Description UserService的实现类
 * @Author 李闯
 * @Date 2018/8/11 20:31
 * @Version 1.0
 **/
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //todo 密码登陆md5
        //因为 这里的比对密码是MD5加密后的，所以比对加密后的密码
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username,md5Password);
        if (user==null){
            //如果用户名不存在的话上面就返回了，到了这里用户名是肯定存在的，只有密码错误
            return ServerResponse.createByErrorMessage("密码错误！");
        }
        //就是不让前台能够获取这个密码，尽管是加密的也不想让前端能够获取的到，其实就是为了客户的安全性着想的。
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user){
        ServerResponse validResponse =this.checkValid(user.getUsername(),Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }

        validResponse =this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        //将结果插入到数据库
        int resultCount = userMapper.insert(user);
        if (resultCount == 0){
            //如果插入的数据是0，就代表插入的数据没插入成功，返回一个异常
            return ServerResponse.createByErrorMessage("注册失败！");
        }
        return ServerResponse.createBySuccessMessage("注册成功！");
    }

    //生名一个复用的方法，用于上面的调用不那么麻烦
    public ServerResponse<String> checkValid(String str,String type){
        //判断type是不是空的，也就是用户名和密码是不是空的，notBlank认为" "是false 而NotEmpty认为" "是true
        if (StringUtils.isNotBlank(type)){
            //当类型不为空才开始校验
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0){
                        return ServerResponse.createByErrorMessage("用户名已存在");
                    }
                }
                if (Const.EMAIL.equals(type)){
                    int resultCount = userMapper.checkEmail(str);
                    if (resultCount > 0){
                        return ServerResponse.createByErrorMessage("Email已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误！");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    //忘记密码返回重置密码的问题
    public ServerResponse selectQuestion(String username){
        //先检查获取的用户名是否存在
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            //使用checkValid方法 如果code不为0即不为success不会调用该方法，因为isSuccess是只有status为0才会返回true，
            //所以checkValid只有当校验成功时才会调用该方法，checkValid也就是校验成功就是用户名不存在。
            //用户不存在
            ServerResponse.createByErrorMessage("用户不存在！");
        }
        String question = userMapper.selectQuestionByUser(username);

        //如果该用户的密保问题不为空
        if (StringUtils.isNotBlank(question)){
            //将问题传给前端
            return ServerResponse.createBySuccess(question);
        }
        //否则提示密码问题为空
        return ServerResponse.createByErrorMessage("找回密码的问题是空的！");
    }

    //检测忘记密码的填写问题答案是否是正确的
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        System.out.printf(String.valueOf(resultCount));
        if (resultCount>0){
            //说明问题及答案是这个用户的，并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            //我们要将forgetToken 放到本地cache中，并且设置他的有效期
            //token_ 前缀可以理解为前缀或者是一个namespace
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误！");
    }

    //重置密码
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        if (StringUtils.isBlank(forgetToken)){
            ServerResponse.createByErrorMessage("参数错误，Token需要传递");
        }
        //我们需要校验username.因为我们在拼接token时，我们用的是token_username 如果username是空的。那么他就是一个没有变量控制的命名
        //先检查获取的用户名是否存在
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            //使用checkValid方法 如果code不为0即不为success不会调用该方法，因为isSuccess是只有status为0才会返回true，
            //所以checkValid只有当校验成功时才会调用该方法，checkValid也就是校验成功就是用户名不存在。
            //用户不存在
            ServerResponse.createByErrorMessage("用户不存在！");
        }

        //从cache中获取token
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);//token应做成一个常量，没有做成常量是为了加深印象
        //对cache中的token进行校验
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或过期");
        }

        //判断token是否相同
        if (StringUtils.equals(forgetToken,token)){
            //校验通过，开始更新密码
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            //定义剩余行数
            int rawCount = userMapper.updatePasswordByUsername(username,md5Password);

            //如果更新的生效行数大于0
            if (rawCount > 0){
                //修改密码成功
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            //如果两个token不同
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    //登录状态的重置密码
    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew, User user){
        //防止横向越权，要校验一下这个用户的旧密码一定是这个用户，因为我们会查询一个count，如果不指定Id,那么查询出来的结果count肯定大于0，true
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());

        if (resultCount==0){
            //旧密码错误
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        //反之成功，设置密码为新密码
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        //因为session中的数据在未来扩展的时候可能是个缩小版，可能有其他的字段，根据主键更新，选择性的更新
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount>0){
            //密码更新成功
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    //更新用户信息
    public ServerResponse<User> updateInformation(User user){
        //username不能被更新
        //email也要进行一个校验，校验新的email是否已经存在，并且存在的email相同的话，不能是我们当前的用户的
        //也就是说我们这个email校验是否存在时，不校验是不是当前的用户，那么当同一个用户他的email没有传过来的时候，我们会校验email已经存在
        //没有跟userId进行关联，那么这个校验就会产生一个逻辑错误，所以要写一个单独的chekEmail
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("Email已经存在，请更换Email，尝试重新更新");

        }
        //声明一个updateuser对象，只用于用户更新
        User updateUser = new User();
        updateUser.setUsername(user.getUsername());
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        //通过这种方式，只更新以上几个字段，
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount>0){
            //更新成功
            return ServerResponse.createBySuccess("更新个人信息成功！",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    //获取个人信息
    public  ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        //如果获取到信息了将密码置空，防止前端可以取到
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    //校验当前用户是不是管理员
    public ServerResponse checkAdminRole(User user){
        if (user!=null&&user.getRole().intValue()== Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
