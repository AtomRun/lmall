package com.leeup.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName CartVo
 * @Description 拥有CartProductVo的集合
 * @Author李闯
 * @Date 2018/9/11 17:56
 * @Version 1.0
 **/
@Data
public class CartVo {
    private List<CartProductVo> cartProductVoList;
    private BigDecimal cartTotalPrice;//购物车总价
    private Boolean allChecked;//是否已经都勾选
    private String imageHost;//图片host
}
