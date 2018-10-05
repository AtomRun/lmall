package com.leeup.common;

import com.google.common.collect.Sets;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.Set;

/**
 * @ClassName Const
 * @Description TODO
 * @Author 李闯
 * @Date 2018/8/11 21:44
 * @Version 1.0
 **/
public class Const {

    public final static String CURRENT_USER = "currentUser";//User的常量
    public final static String EMAIL = "email";//Email的常量
    public final static String USERNAME = "username";//username的常量

    public interface Cart{
        int CHECKED = 1;//购物车选中状态
        int UNCHECKED = 0;//购物车中未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";//限制失败
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";//限制成功
    }
    public interface ProductListOrderBy{
        //与前端约定的常量
        //约定使用下划线做一个分割，后面代表排序规则，前面orderby的字段
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
        //使用Set的原因，Set的Contains事件复杂度是o 1，而List的是o  n
    }

    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }

    public enum ProductStatusEnum{

        ON_SALE(1,"在线");

        private int code;
        private String value;

        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已支付"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSED(60,"订单关闭");

        private int code;
        private String value;


        OrderStatusEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public static OrderStatusEnum codeOf(int code){
            for (OrderStatusEnum orderStatusEnum : values()){
                if (orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

    }

    public interface AlipayCallBack{
        String TRADE_STATUS_WAIT_BUYER_PAY="WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatformEnum{

        ALIPAY(1,"支付宝");

        private int code;
        private String value;

        PayPlatformEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public enum PaymentTypeEnum{

        NOLINE_PAY(1,"在线支付");
        private int code;
        private String value;

        PaymentTypeEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public static PaymentTypeEnum codeOf(int code){
            for (PaymentTypeEnum paymentTypeEnum : values()){
                if (paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
}
