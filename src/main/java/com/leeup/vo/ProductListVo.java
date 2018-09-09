package com.leeup.vo;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @ClassName ProductListVo
 * @Description List不需要完整的Product那么详细的内容，因为只是一个List而不是详情，所以取出几个需要的属性
 * 单独封装一个listvalueobject对象
 * @Author李闯
 * @Date 2018/8/31 13:00
 * @Version 1.0
 **/
@Data
public class ProductListVo {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private BigDecimal price;
    private Integer status;

    private String imageHost;
}
