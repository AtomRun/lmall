package com.leeup.service;

import com.github.pagehelper.PageInfo;
import com.leeup.common.ServerResponse;
import com.leeup.pojo.Shipping;

/**
 * @ClassName IShippingService
 * @Description TODO
 * @Author李闯
 * @Date 2018/9/16 11:51
 * @Version 1.0
 **/
public interface IShippingService {

    /**
     * @Author 李闯
     * @Description 新建收货地址
     * @Date 14:07 2018/9/16
     * @Param [userId, shipping]
     * @return com.leeup.common.ServerResponse
     **/
    ServerResponse add(Integer userId, Shipping shipping);

    /**
     * @Author 李闯
     * @Description 删除地址接口
     * @Date 14:31 2018/9/16
     * @Param [userId, shippingId]
     * @return com.leeup.common.ServerResponse<java.lang.String>
     **/
    ServerResponse<String> del(Integer userId,Integer shippingId);

    /**
     * @Author 李闯
     * @Description 更新地址
     * @Date 14:54 2018/9/16
     * @Param [userId, shipping]
     * @return com.leeup.common.ServerResponse
     **/
    ServerResponse update(Integer userId,Shipping shipping);

    /**
     * @Author 李闯
     * @Description 查询地址
     * @Date 15:04 2018/9/16
     * @Param [userId, shippingId]
     * @return com.leeup.common.ServerResponse<com.leeup.pojo.Shipping>
     **/
    ServerResponse<Shipping> select(Integer userId,Integer shippingId);

    /**
     * @Author 李闯
     * @Description 列表分页查看故障地址页面
     * @Date 15:31 2018/9/16
     * @Param [userId, pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);
}
