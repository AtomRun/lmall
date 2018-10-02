package com.leeup.service;

import com.leeup.common.ServerResponse;

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
    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);
}
