package com.leeup.dao;

import com.google.common.collect.Lists;
import com.leeup.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    /**
     * @Author 李闯
     * @Description 根据用户Id和chanpinId查询购物车
     * @Date 19:19 2018/9/10
     * @Param [userId, productId]
     * @return com.leeup.pojo.Cart
     **/
    Cart selectCartByUserIdProductId(@Param("userId")Integer userId, @Param("productId")Integer productId);

    /**
     * @Author 李闯
     * @Description 根据userId查询出cart对象
     * @Date 18:09 2018/9/11
     * @Param [userId]
     * @return java.util.List<com.leeup.pojo.Cart>
     **/
    List<Cart> selectCartByUserId(Integer userId);

    /**
     * @Author 李闯
     * @Description 根据userId查询购物车产品选中请情况
     * @Date 22:47 2018/9/15
     * @Param [userId]
     * @return int
     **/
    int selectCartProductCheckedStatusByUserId(Integer userId);

    /**
     * @Author 李闯
     * @Description 删除购物车中商品方法
     * @Date 0:08 2018/9/16
     * @Param [userId, productList]
     * @return int
     **/
    int deleteByUserIdProductIds(@Param("userId") Integer userId, @Param("productIdList")List<String> productIdList);

    /**
     * @Author 李闯
     * @Description 使用相同的dao层，根据Controller传的不同的参数，我们把它置成全选或者全反选
     * @Date 10:05 2018/9/16
     * @Param [userId, checked]
     * @return int
     **/
    int checkOrCheckedProduct(@Param("userId") Integer userId,@Param("productId")Integer productId,@Param("checked") Integer checked);

    /**
     * @Author 李闯
     * @Description 查询购物车中的产品数量
     * @Date 10:26 2018/9/16
     * @Param [userId]
     * @return int
     **/
    int selectCartProductCount(Integer userId);
}