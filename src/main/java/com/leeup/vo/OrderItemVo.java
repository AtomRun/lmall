package com.leeup.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName OrderItemVo
 * @Description TODO
 * @Author李闯
 * @Date 2018/10/4 16:26
 * @Version 1.0
 **/
@Data
public class OrderItemVo {

    private Long orderNo;
    private Integer productId;

    private String productName;
    private String productImage;
    private BigDecimal currentUnitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;
    private String createTime;
}
