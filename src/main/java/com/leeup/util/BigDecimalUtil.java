package com.leeup.util;

import java.math.BigDecimal;

/**
 * @ClassName BigDecimalUtil
 * @Description BigDecimal转换工具类
 * @Author李闯
 * @Date 2018/9/13 22:00
 * @Version 1.0
 **/
public class BigDecimalUtil {

    //为了不能让他在外部实例化，我们要放一个私有构造器
    private BigDecimalUtil(){

    }

    //加减乘除
    public static BigDecimal add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));//将v1转化成String再调用BigDecimal的String构造器
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.add(b2);
    }

    public static BigDecimal sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));//将v1转化成String再调用BigDecimal的String构造器
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.subtract(b2);
    }

    public static BigDecimal mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));//将v1转化成String再调用BigDecimal的String构造器
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.multiply(b2);
    }

    public static BigDecimal div(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));//将v1转化成String再调用BigDecimal的String构造器
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//四舍五入，保留两位小数

        //除法时我们要进行某种策略，例如四舍五入或者保留几位小数
    }
}
