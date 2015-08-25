package net.thinkbase.util;

import java.text.* ;
/**
 * 一个辅助进行数字处理的类
 * @author thinkbase.net
 */
public class NumberUtility {
    
    /** 将字符串类型转化为Number类型
     * @param value 数字字符串
     * @return Number
     * @exception ParseException
     */    
    public static Number string2Num (String value) throws ParseException {
        String sVal = value;
        NumberFormat ofmt = NumberFormat.getNumberInstance();
        Number num ;
        //格式化输入的字符串
        if (sVal==null) sVal="";    //不能是null
        sVal = sVal.trim();         //去掉多余空格
        if (sVal.length()==0) {
            num=null;
        } else {
            while (sVal.charAt(0)=='+') sVal=sVal.substring(1);    //去掉开头的"+"号
            //开始解析 ...
            num = ofmt.parse(sVal);
        }
        if (null==num){
        	throw new ParseException("Error input string: '" + value + "'", 0);
        }else{
        	return (num) ;
        }
    }
    
    /**
     * 格式化输出字符串
     * @param num
     * @param format 格式字符串, 默认格式为 "#,##0.00"
     * @return
     */
    public static String num2String (Number num, String format){
        Number n = num;
        if (null==n) n = new Long(0);
        String fmt = format;
        if (null==fmt) fmt = "#,##0.00";    //默认的格式
        DecimalFormat df = new DecimalFormat(fmt);
        String s = df.format(n);
        return s;
    }
    /**
     * 格式化输出字符串, 使用默认格式("#,##0.00")
     * @param num
     * @return
     */
    public static String num2String (Number num){
        return num2String(num, null);
    }
}
