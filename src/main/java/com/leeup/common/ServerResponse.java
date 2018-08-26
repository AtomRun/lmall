package com.leeup.common;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * @ClassName ServerResponse
 * @Description 通用端的数据响应对象
 * @Author 李闯
 * @Date 2018/8/11 20:32
 * @Version 1.0
 **/
//泛型代表了响应数据对象封装的是什么
//最大的好处就是，返回时可以指定指定泛型的内容，也可以不指定泛型的强制类型在某些情况下，例如正确的时候可能封装的是String,错误的时候，封装的是map，或者list，
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)//该注解意思是 序列化json时，如果是null的对象key也会消失
public class ServerResponse<T> implements Serializable {


    private T data;//使用T做泛型，data就可以通用了，
    private int status;
    private String msg;

    private ServerResponse(int status){
        this.status = status;
    }

    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    private ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }

    //如果不加处理，这个字段也会出现在json字符串中，
    //加了该注解 这个字段序列化之后就不会显示在json中,使之不在json序列化结果当中，而下面具有public get开头的方法都会显示在json中
    @JsonIgnore
    public boolean isSuccess(){
        //如果status为0，我们就返回true,如果不是0就返回false
        return this.status == ResponseCode.SUCCESS.getCode();
    }
    public int getStatus(){
        return status;
    }
    public T getData(){
        return data;
    }
    public String getMsg(){
        return msg;
    }
    //创建某个对象根据成功的,这个方法不需要 传参数，就是调用返回一个status的
    public static <T> ServerResponse<T> createBySuccess(){
        //也就是说我们的code为0响应是OK的
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    //也是根据成功创建，但是成功后返回一个文本，供前端提示使用
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    //我要创建一个成功的服务器响应，要把data填充进去
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    //传送了消息和数据
    public static <T>ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    //错误返回错误码和错误描述
    public static <T>ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    //错误创建 时生成错误代码和错误提示
    public static <T>ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    //将code变成变量的方法。很多时候，我们需要对外暴露出一个 需要登录/参数出现错误，服务端的响应编码和消息
    public static <T>ServerResponse<T> createByErrorCodeMessage(Integer errorCode,String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }

}
