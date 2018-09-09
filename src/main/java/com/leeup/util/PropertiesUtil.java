package com.leeup.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/*
 * @Author 李闯
 * @Description 使用这个类来读取配置，通过读取文件可以将imagehost进行set
 * @Date 21:12 2018/8/30
 * @Param
 * @return
 **/
public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties props;

    /*
     * 静态代码块执行顺序，优于普通代码快，普通代码块优于构造代码块
     * 构造代码块构造对象每次都会执行，但是静态代码块只会执行一次，会在类被加载的时候执行，且只会执行一次，一般我们都用他做初始化静态变量
     * 比如imagehost 我们就是要初始化它作为一个静态变量
     **/

    //tomcat启动时加载的配置
    //静态代码块
    static {
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            //创建输入流，找到当前这个类的位置，通过getResourceAsStream 依靠流获取源文件
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            logger.error("配置文件读取异常",e);
        }
    }

    public static String getProperty(String key){
        String value = props.getProperty(key.trim());//避免两边的空格
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key,String defaultValue){

        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }



}
