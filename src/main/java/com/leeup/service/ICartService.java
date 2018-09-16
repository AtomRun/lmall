package com.leeup.service;

import com.leeup.common.ServerResponse;
import com.leeup.vo.CartVo;

/**
 * @ClassName ICartService
 * @Description TODO
 * @Author李闯
 * @Date 2018/9/10 18:57
 * @Version 1.0
 **/
public interface ICartService {

    /**
     * @Author 李闯
     * @Description 购物车添加的方法
     * @Date 23:24 2018/9/15
     * @Param [userId, productId, count 购物车商品数量]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    /**
     * @Author 李闯
     * @Description 更新方法
     * @Date 23:34 2018/9/16
     * @Param [userId, productId, count]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count);

    /**
     * @Author 李闯
     * @Description 删除产品
     * @Date 0:17 2018/9/16
     * @Param [userId, productIds]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);

    /**
     * @Author 李闯
     * @Description 购物车列表接口
     * @Date 9:58 2018/9/16
     * @Param [userId]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    ServerResponse<CartVo> list(Integer userId);

    /**
     * @Author 李闯
     * @Description 更新购物车全选/全反选的状态接口
     * @Date 10:10 2018/9/16
     * @Param [userId, checked]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked);

    /**
     * @Author 李闯
     * @Description 查询购物车数量的接口
     * @Date 10:43 2018/9/16
     * @Param [userId]
     * @return com.leeup.common.ServerResponse<java.lang.Integer>
     **/
    ServerResponse<Integer> getCartProductCount(Integer userId);
}
