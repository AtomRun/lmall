package com.leeup.controller.portal;

import com.github.pagehelper.PageInfo;
import com.leeup.common.Const;
import com.leeup.common.ResponseCode;
import com.leeup.common.ServerResponse;
import com.leeup.pojo.Shipping;
import com.leeup.pojo.User;
import com.leeup.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @ClassName ShippingController
 * @Description 地址模块Controller
 * @Author李闯
 * @Date 2018/9/16 11:50
 * @Version 1.0
 **/
@Controller
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;

    //查询集合，这个集合是一个列表页，我们要进行一个分页

    /**
     * @Author 李闯
     * @Description 增加收货地址的方法
     * @Date 11:53 2018/9/16
     * @Param [session,shipping springMVC绑定中的一种方式，就是直接绑定对象，
     * 这种写法和直接传里面的字段是一样的，但是我们在这个接口当中，地址Id和userId不需要前端传
     * 并且地址Id是新增的，前端也拿不到这个Shipping的ID，两个time也不需要前端传，我们只需要前端传这些填充地址的数据，我们直接使用
     * 对象接收这种方式，对象数据绑定，使用对象来承载里面的字段，使它可以接收到值，springmvc会自动生成该对象，并把属性赋值上，否则我们
     * 需要写很多字段]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(),shipping);
    }

    /**
     * @Author 李闯
     * @Description 删除地址
     * @Date 14:17 2018/9/16
     * @Param [session, shppingId]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpSession session,Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.del(user.getId(),shippingId);
    }

    /**
     * @Author 李闯
     * @Description 更新收货地址接口
     * @Date 11:53 2018/9/16
     * @Param [session,shipping]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(),shipping);
    }

    /**
     * @Author 李闯
     * @Description 删除地址
     * @Date 14:17 2018/9/16
     * @Param [session, shppingId]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpSession session,Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(),shippingId);
    }


    /**
     * @Author 李闯
     * @Description 分页展示地址接口
     * @Date 15:06 2018/9/16
     * @Param [pageNum, pageSize, session]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                         HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.list(user.getId(),pageNum,pageSize);
    }
}
