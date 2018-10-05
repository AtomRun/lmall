package com.leeup.dao;

import com.leeup.pojo.Order;
import org.apache.ibatis.annotations.Param;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    /**
     * @Author 李闯
     * @Description 根据用户id和订单编号查询是否有该订单
     * @Date 22:29 2018/9/20
     * @Param [userId, orderNo]
     * @return com.leeup.pojo.Order
     **/
    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId,@Param("orderNo") Long orderNo);

    /**
     * @Author 李闯
     * @Description 根据订单号查询是否有该订单
     * @Date 17:10 2018/10/2
     * @Param [orderNo]
     * @return com.leeup.pojo.Order
     **/
    Order selectByOrderNo(Long orderNo);

    /**
     * @Author 李闯
     * @Description 通过userId获取订单
     * @Date 17:08 2018/10/5
     * @Param [Integer userId]
     * @return java.util.List<com.leeup.pojo.Order>
     **/
    List<Order> selectByUserId(Integer userId);

    /**
     * @Author 李闯
     * @Description 查询所有Order
     * @Date 20:51 2018/10/5
     * @Param []
     * @return java.util.List<com.leeup.pojo.Order>
     **/
    List<Order> selectAllOrder();
}