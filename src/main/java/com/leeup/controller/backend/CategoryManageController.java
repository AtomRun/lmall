package com.leeup.controller.backend;

import com.leeup.common.Const;
import com.leeup.common.ResponseCode;
import com.leeup.common.ServerResponse;
import com.leeup.pojo.User;
import com.leeup.service.ICategoryService;
import com.leeup.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpSession;

/**
 * @ClassName CategoryManageController
 * @Description 分离管理模块Controller层
 * @Author李闯
 * @Date 2018/8/27 12:56
 * @Version 1.0
 **/
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    //增加分类接口
    //要判断登录用户是不是管理员，我们需要Session
    //parentId 父节点，我们要对它做一个限制，如果前端没有传默认的值，我们设置一个默认值0，0是分类的根节点
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){
        //判断登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请重新登录");
        }
        //校验是不是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            //增加我们处理分类的逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        }else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    //更新类别名的接口
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        //判断登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请重新登录");
        }
        //校验是不是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //更新分类名的逻辑
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    //跟据我们传的categoryId获取当前categoryId下面子节点的category信息，并且是平级的，不递归
    //如果category没传默认为0，0代表category的根节点
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
        //判断登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请重新登录");
        }
        //校验是不是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的category信息，并且不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }


    //获取当前categoryId，并且递归查询他的字节点的categoryId
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
        //判断登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请重新登录");
        }
        //校验是不是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //查询当前节点的id和递归子节点
            //递归子节点的id就是指 假设parentId是0，下面有个子孩子 10000 下面还有个子孩子 100000
            //我们这个接口传一万过来，返回十万给他，如果传0过来，一万和十万都要返回
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }
}
