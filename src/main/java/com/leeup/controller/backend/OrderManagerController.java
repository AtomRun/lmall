package com.leeup.controller.backend;

import com.github.pagehelper.PageInfo;
import com.leeup.common.Const;
import com.leeup.common.ResponseCode;
import com.leeup.common.ServerResponse;
import com.leeup.pojo.User;
import com.leeup.service.IOrderService;
import com.leeup.service.IUserService;
import com.leeup.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @ClassName OrderManagerController
 * @Description TODO
 * @Author李闯
 * @Date 2018/10/5 17:23
 * @Version 1.0
 **/
@Controller
@RequestMapping("/manage/order")
public class OrderManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    /**
     * @Author 李闯
     * @Description 后台管理订单列表分页
     * @Date 17:25 2018/10/5
     * @Param [session, pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    @ResponseBody
    @RequestMapping("list.do")
    public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);

        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请使用管理员登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //填充我们的业务逻辑
            return iOrderService.manageList(pageNum,pageSize);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * @Author 李闯
     * @Description 后台查看订单详情
     * @Date 17:25 2018/10/5
     * @Param [session, pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    @ResponseBody
    @RequestMapping("detail.do")
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);

        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请使用管理员登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //填充我们的业务逻辑
            return iOrderService.manageDetail(orderNo);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * @Author 李闯
     * @Description 后台根据订单编号搜索订单
     * @Date 17:25 2018/10/5
     * @Param [session, pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    @ResponseBody
    @RequestMapping("search.do")
    public ServerResponse<PageInfo> orderSearch(HttpSession session, Long orderNo,@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                               @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);

        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请使用管理员登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //填充我们的业务逻辑
            return iOrderService.manageSearch(orderNo,pageNum,pageSize);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * @Author 李闯
     * @Description 后台对订单进行发货
     * @Date 17:25 2018/10/5
     * @Param [session, pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    @ResponseBody
    @RequestMapping("sendGoods.do")
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);

        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请使用管理员登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //填充我们的业务逻辑
            return iOrderService.sendGoods(orderNo);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

}
