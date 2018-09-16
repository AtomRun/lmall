package com.leeup.controller.portal;

import com.leeup.common.Const;
import com.leeup.common.ResponseCode;
import com.leeup.common.ServerResponse;
import com.leeup.pojo.User;
import com.leeup.service.ICartService;
import com.leeup.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @ClassName CartController
 * @Description 购物车控制类
 * @Author李闯
 * @Date 2018/9/9 20:30
 * @Version 1.0
 **/
@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    /**
     * @Author 李闯
     * @Description 查询购物车
     * @Date 20:32 2018/9/16
     * @Param [session 只需要登录用户即可查看]
     * @return com.leeup.common.ServerResponse
     **/
    @ResponseBody
    @RequestMapping("list.do")
    public ServerResponse<CartVo> list(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //购物车的核心逻辑
        return iCartService.list(user.getId());
    }

    /**
     * @Author 李闯
     * @Description 添加商品到购物车中
     * @Date 20:32 2018/9/9
     * @Param [session, count 购物车商品数量, productId]
     * @return com.leeup.common.ServerResponse
     **/
    @ResponseBody
    @RequestMapping("add.do")
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //购物车的核心逻辑
        return iCartService.add(user.getId(),productId,count);
    }


    /**
     * @Author 李闯
     * @Description 更新购物车，更新购物车中产品的数量，用+或者人工添加的时候
     * @Date 20:32 2018/9/16
     * @Param [session, count 购物车商品数量, productId]
     * @return com.leeup.common.ServerResponse
     **/
    @ResponseBody
    @RequestMapping("update.do")
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }

    /**
     * @Author 李闯
     * @Description 购物车中删除产品的接口 [删除产品的时候不需要数量，并且删除产品的时候可能是一下删除多个，例如选择了多个产品一下点删除，我们这里和
     * 前端的约定就是传一个字符串，然后使用逗号分隔productId,]
     * @Date 20:32 2018/9/16
     * @Param [session, productIds]
     * @return com.leeup.common.ServerResponse
     **/
    @ResponseBody
    @RequestMapping("deleteProduct.do")
    public ServerResponse<CartVo> deleteProduct(HttpSession session, String productIds){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    //全选【在service层中封装一个公用的方法，在 Controller中传不同的参数即可】

    /**
     * @Author 李闯
     * @Description 全选的状态
     * @Date 10:12 2018/9/16
     * @Param [session, productIds]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    @ResponseBody
    @RequestMapping("selectAll.do")
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),null,Const.Cart.CHECKED);
    }


    /**
     * @Author 李闯
     * @Description 全反选的状态
     * @Date 10:12 2018/9/16
     * @Param [session, productIds]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    @ResponseBody
    @RequestMapping("unSelectAll.do")
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),null,Const.Cart.UNCHECKED);
    }


    /**
     * @Author 李闯
     * @Description 单独选某个产品
     * @Date 10:12 2018/9/16
     * @Param [session, productId]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    @ResponseBody
    @RequestMapping("select.do")
    public ServerResponse<CartVo> select(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),productId,Const.Cart.CHECKED);
    }


    /**
     * @Author 李闯
     * @Description 单独反选某个产品
     * @Date 10:12 2018/9/16
     * @Param [session, productId]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    @ResponseBody
    @RequestMapping("unSelect.do")
    public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),productId,Const.Cart.UNCHECKED);
    }


    //查询当前用户的购物车中产品数量，放在页面右上角显示，如果一个产品有十个，那么数量就是10，按照总体数量来算
    @ResponseBody
    @RequestMapping("getCartProductCount.do")
    public ServerResponse<Integer> getCartProductCount(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createBySuccess(0);//未登录用户看到购物车的数量为0
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
