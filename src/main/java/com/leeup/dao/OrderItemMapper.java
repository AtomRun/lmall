package com.leeup.dao;

import com.leeup.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    /**
     * @Author 李闯
     * @Description 获取订单详情根据userId和orderNo
     * @Date 17:15 2018/10/5
     * @Param [orderNo, userId]
     * @return java.util.List<com.leeup.pojo.OrderItem>
     **/
    List<OrderItem> getByOrderNoUserId(@Param("orderNo")Long orderNo,@Param("userId")Integer userId);

    /**
     * @Author 李闯
     * @Description 批量插入
     * @Date 16:04 2018/10/4
     * @Param [orderItemList]
     * @return void
     **/
    void batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);

    /**
     * @Author 李闯
     * @Description 获取订单详情根据userId
     * @Date 17:15 2018/10/5
     * @Param [orderNo, userId]
     * @return java.util.List<com.leeup.pojo.OrderItem>
     **/
    List<OrderItem> getByOrderNo(@Param("orderNo")Long orderNo);

}