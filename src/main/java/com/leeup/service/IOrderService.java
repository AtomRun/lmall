package com.leeup.service;

import com.github.pagehelper.PageInfo;
import com.leeup.common.ServerResponse;
import com.leeup.vo.OrderVo;

import java.util.Map;

/**
 * @ClassName IOrderService
 * @Description TODO
 * @Author李闯
 * @Date 2018/9/20 22:23
 * @Version 1.0
 **/
public interface IOrderService {
    /**
     * @Author 李闯
     * @Description 支付的接口
     * @Date 16:30 2018/9/22
     * @Param [orderNo, userId, path]
     * @return com.leeup.common.ServerResponse
     **/

    ServerResponse pay(Long orderNo, Integer userId, String path);

    /**
     * @Author 李闯
     * @Description 支付宝回调验证
     * @Date 17:34 2018/10/2
     * @Param [params]
     * @return com.leeup.common.ServerResponse
     **/
    ServerResponse aliCallBack(Map<String,String> params);

    /**
     * @Author 李闯
     * @Description 查询是否有该订单
     * @Date 17:54 2018/10/2
     * @Param [userId, orderNo]
     * @return com.leeup.common.ServerResponse
     **/
    ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);

    /**
     * @Author 李闯
     * @Description 创建订单
     * @Date 0:03 2018/10/5
     * @Param [userId, shippingId]
     * @return com.leeup.common.ServerResponse
     **/
    ServerResponse createOrder(Integer userId,Integer shippingId);

    /**
     * @Author 李闯
     * @Description 取消订单
     * @Date 0:22 2018/10/5
     * @Param [userId, orderNo]
     * @return com.leeup.common.ServerResponse<java.lang.String>
     **/
    ServerResponse<String> cancel(Integer userId,Long orderNo);

    /**
     * @Author 李闯
     * @Description 获取购物车中的选中的产品详情
     * @Date 16:55 2018/10/5
     * @Param [userId]
     * @return com.leeup.common.ServerResponse
     **/
    ServerResponse getOrderCartProduct(Integer userId);

    /**
     * @Author 李闯
     * @Description 获取订单详情
     * @Date 17:02 2018/10/5
     * @Param [userId, orderNo]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.OrderVo>
     **/
    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    /**
     * @Author 李闯
     * @Description 个人中心-查看我的订单list列表接口
     * @Date 17:21 2018/10/5
     * @Param [userId, pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    ServerResponse<PageInfo> getOrderList(Integer userId, Integer pageNum, Integer pageSize);

    /**
     * @Author 李闯
     * @Description 后台管理员订单列表展示
     * @Date 20:58 2018/10/5
     * @Param [pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);

    /**
     * @Author 李闯
     * @Description  后台查看订单详情
     * @Date 21:08 2018/10/5
     * @Param [orderNo]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.OrderVo>
     **/
    ServerResponse<OrderVo> manageDetail(Long orderNo);

    /**
     * @Author 李闯
     * @Description 根据订单号搜索订单
     * @Date 21:18 2018/10/5
     * @Param [orderNo, pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);

    /**
     * @Author 李闯
     * @Description 后台对订单进行发货
     * @Date 21:24 2018/10/5
     * @Param [orderNo]
     * @return com.leeup.common.ServerResponse<java.lang.String>
     **/
    ServerResponse<String> sendGoods(Long orderNo);
}
