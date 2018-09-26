package com.mmall.util;

import java.math.BigDecimal;

/**
 * Create by zhouxin
 **/
public class BigDecimalUtil {

    private BigDecimalUtil(){

    }


    public static BigDecimal add(double v1,double v2){
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        return bigDecimal1.add(bigDecimal2);
    }

    public static BigDecimal sub(double v1,double v2){
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        return bigDecimal1.subtract(bigDecimal2);
    }

    public static BigDecimal mul(double v1,double v2){
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        return bigDecimal1.multiply(bigDecimal2);
    }

    public static BigDecimal div(double v1,double v2){
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        return bigDecimal1.divide(bigDecimal2,2,BigDecimal.ROUND_HALF_UP);
    }
}
