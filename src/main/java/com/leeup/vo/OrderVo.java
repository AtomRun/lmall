package com.leeup.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName OrderVo
 * @Description Order组装实体类
 * @Author李闯
 * @Date 2018/10/4 16:22
 * @Version 1.0
 **/
@Data
public class OrderVo {
    private Long orderNo;
    private BigDecimal payment;
    private Integer paymentType;
    private String paymentTypeDesc;
    private Integer postage;
    private Integer status;
    private String statusDesc;
    private String paymentTime;
    private String sendTime;
    private String endTime;
    private String closeTime;
    private String createTime;

    //订单明细
    private List<OrderItemVo> orderItemVoList;

    private String imageHost;
    private Integer shippingId;
    private String receiverName;

    //收货人信息
    private ShippingVo shippingVo;


}
