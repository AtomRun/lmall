package com.leeup.util;


import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * @ClassName DateTimeUtil
 * @Description 时间转换工具类
 * @Author李闯
 * @Date 2018/8/30 22:37
 * @Version 1.0
 **/
public class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //joda-time

    //str->Date
    /*
     * @Author 李闯
     * @Description 字符串转日期
     * @Date 22:38 2018/8/30
     * @Param [dateTimeStr 时间字符串, formatStr 转成date类型字符串的format格式]
     * @return java.util.Date
     **/
    public static Date strToDate(String dateTimeStr,String formatStr){
        //接收要修改成字符串的格式
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormat.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    //Date->str
    /*
     * @Author 李闯
     * @Description 日期转字符串
     * @Date 22:50 2018/8/30
     * @Param [date, formatStr]
     * @return java.lang.String
     **/
    public static String dateToStr(Date date,String formatStr){
        if (date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);

        //会返回指定字符串格式的
    }

    public static Date strToDate(String dateTimeStr){
        //接收要修改成字符串的格式
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormat.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date){
        if (date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);

        //会返回指定字符串格式的
    }
}
