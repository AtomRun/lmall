package com.leeup.vo;

import com.leeup.pojo.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName OrderProductVo
 * @Description TODO
 * @Author李闯
 * @Date 2018/10/5 16:35
 * @Version 1.0
 **/
@Data
public class OrderProductVo {

    private List<OrderItemVo> orderItemVoList;
    private BigDecimal productTotalPrice;
    private String imageHost;
}
