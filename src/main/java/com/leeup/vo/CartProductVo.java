package com.leeup.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName CartProductVo
 * @Description 结合了产品和购物车的一个抽象对象
 * @Author李闯
 * @Date 2018/9/11 17:51
 * @Version 1.0
 **/
@Data
public class CartProductVo {

    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String productSubTitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus;
    private BigDecimal productTotalPrice;
    private Integer productStock;//产品库存
    private Integer productChecked;//此商品在购物车中是否勾选

    private String limitQuantity;//限制数量的一个返回结果
}
