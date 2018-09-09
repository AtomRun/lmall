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
    public interface ProductListOrderBy{
        //与前端约定的常量
        //约定使用下划线做一个分割，后面代表排序规则，前面orderby的字段
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
        //使用Set的原因，Set的Contains事件复杂度是o 1，而List的是o n
    }

    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }

    public enum ProductStatusEnum{

        ON_SALE(1,"在线");

        private String value;
        private int code;

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
}
