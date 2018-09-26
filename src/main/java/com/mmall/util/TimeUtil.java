package com.mmall.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Create by zhouxin
 **/
public class TimeUtil {
    //str->data,data->str

    private final static String defaultFormat = "yyyy-MM-dd HH:mm:ss";

    public static Date strToTime(String time,String timeformate){
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(timeformate);
        DateTime dateTime = dateTimeFormat.parseDateTime(time);
        return dateTime.toDate();
    }

    public static String timeToStr(Date date,String format){
        if(date==null){
            return "";
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    public static Date strToTime(String time){
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(defaultFormat);
        DateTime dateTime = dateTimeFormat.parseDateTime(time);
        return dateTime.toDate();
    }

    public static String timeToStr(Date date){
        if(date==null){
            return "";
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(defaultFormat);
    }

}
