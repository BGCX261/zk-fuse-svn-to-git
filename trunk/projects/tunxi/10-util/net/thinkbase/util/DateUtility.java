package net.thinkbase.util;

import java.text.* ;
import java.util.* ;
/**
 * 一些与日期时间相关的处理过程
 * @author thinkbase.net
 */
public class DateUtility {
    
    /**
     * 获得当前的时间
     */
    public static Date now() {
        return (new Date());
    }
    
    /**
     * 格式化日期到指定格式(采用的地区编码为Locale.US)<br>
     * (具体格式参见java.text.SimpleDateFormat的有关文档)
     * @param date 日期时间对象
     * @param fmt 格式字符串
     * @return 经过格式化的日期字符串
     */
    public static String date2String (Date date, String fmt) {
        return( date2String(date,fmt,Locale.US) ) ;
    }
    /**
     * 格式化日期到指定格式<br>
     * (具体格式参见java.text.SimpleDateFormat的有关文档)
     * @param date 日期时间对象
     * @param fmt 格式字符串
     * @param loc 地区编码
     * @return 经过格式化的日期字符串
     */
    public static String date2String (Date date, String fmt, Locale loc) {
        SimpleDateFormat ofmt = new SimpleDateFormat(fmt, loc) ;
        return( ofmt.format(date) ) ;
    }
    /**
     * 将标准的日期/时间字符串自动转换为日期<br>
     * 目前只处理6种格式 :<br>
     * yyyy/MM/dd HH:mm:ss, yyyy/M/d H:m:s <br>
     * yyyy/MM/dd HH:mm, yyyy/M/d H:m <br>
     * yyyy/MM/dd, yyyy/M/d  <br>
     * 其中日期的分隔符除了可以是'/'之外, 还可以是'-'和'.', 但时间分隔符只能是':'! <br>
     * 注意此函数是建立在这样一个事实的基础上:<br>
     * 1)yyyy/M/d H:m:s的模式也可以用于解析yyyy/MM/dd HH:mm:ss模式;<br>
     * 2)实际输入字符串中各部分数字前的空格并不影响解析;(例如:"  2002/ 4/ 06   1: 05")<br>
     * @param dateString 日期时间字符串
     * @return 解析得到的日期时间对象, 如果解析失败, 返回 null
     */
    public static Date string2Date (String dateString) {
        String sDate = dateString;
        String s1="yyyy/M/d H:m:s";
        String s2="yyyy/M/d H:m";
        String s3="yyyy/M/d";
        Date date ;
        //处理头尾空格
        sDate=sDate.trim();
        //首先将'-','.'分隔符替换为'/'分隔符
        sDate=StringUtility.replace(sDate, "-", "/");
        sDate=StringUtility.replace(sDate, ".", "/");
        //通过尝试获得合适的返回值
        date=string2Date(sDate,s1,Locale.US);
        if (date!=null) return(date) ;
        date=string2Date(sDate,s2,Locale.US);
        if (date!=null) return(date) ;
        date=string2Date(sDate,s3,Locale.US);
        if (date!=null) return(date) ;
        //如果不能解析 ...
        return (null) ;
    }

    /**
     * 将日期时间字符串解析到日期时间对象(采用的地区编码为Locale.US)
     * @param sDate 日期时间字符串
     * @param fmt 解析格式字符串
     * @return 解析得到的日期时间对象, 如果解析失败, 返回 null
     */    
    public static Date string2Date (String sDate, String fmt) {
        return (string2Date(sDate,fmt,Locale.US)) ;
    }

    /**
     * 将日期时间字符串解析到日期时间对象
     * @param sDate 日期时间字符串
     * @param fmt 解析格式字符串
     * @param loc 地区编码
     * @return 解析得到的日期时间对象, 如果解析失败, 返回 null
     */    
    public static Date string2Date (String sDate, String fmt, Locale loc) {
        Date date ;
        SimpleDateFormat ofmt = new SimpleDateFormat(fmt, loc);
        try {
            date = ofmt.parse(sDate);
        }
        catch (ParseException ex) {
            date = null ;
        }
        return (date) ;
    }
    
    /**
     * 返回系统约定的无效日期: 1900/01/01 00:00:00
     * @return
     */
    public static Date getInvalidDate(){
        return string2Date("1900/01/01 00:00:00","yyyy/MM/dd HH:mm:ss");
    }
    
    /**
     * 判断日期是否是系统定义的有效日期
     * @param dt
     * @return 大于 1900 年的日期被认为是有效日期
     */
    public static boolean isDateValid(Date dt){
        if (null==dt) return false;
        
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        int year = c.get(Calendar.YEAR);
        return (year > 1900);
    }
    
    /**
     * 将一个包含日期/时间的Date类型变量的时间部分除去
     * @param date
     * @return
     */
    public static Date truncateTime(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * 以指定间隔获得起始日期/时间和结束日期/时间之间的所有日期/时间
     * @param from 其实日期/时间
     * @param to 结束日期/时间
     * @param field 指定间隔 Calendar.YEAR, MONTH, DATE, HOUR, MINUTE, SECOND, 等等
     * @param step 指定步长
     * @return 包含其中日期/时间的数组,
     *         在 from != to 的情况下, 返回数组的头必然是 from, 但是尾可能不包括 to;
     *         如果 from == to, 则返回的数组中只有一个元素 [0] == from == to.
     * <br>如果 from > to, 返回结果中 from 和 to 将会互换
     */
    @SuppressWarnings("unchecked")
    public static Date[] getDateRange(Date from, Date to, int field, int step){
        Calendar calFm = Calendar.getInstance();
        Calendar calTo = Calendar.getInstance();
        if (to.compareTo(from) == 0){
            Date[] ret = new Date[1];
            ret[0] = from;
            return ret;
        }else if (to.compareTo(from) < 0){
            calFm.setTime(to);
            calTo.setTime(from);
        }else{
            calFm.setTime(from);
            calTo.setTime(to);
        }
        List dates = new ArrayList();
        for (Calendar pt=calFm; !pt.after(calTo); pt.add(field, step)){
            dates.add(pt.getTime().clone());
        }
        int size=dates.size();
        Date[] ret = new Date[size];
        for (int i=0; i<size; i++){
            ret[i] = (Date)dates.get(i);
        }
        return ret;
    }
    
    /**
     * 将起始日期/时间加上响应的毫秒, 获得一个新的时间
     * @param dateFm 其实日期/时间
     * @param ms 需要加上的毫秒数
     * @return
     */
    public static Date addTime(Date dateFm, long ms){
        long msFm = dateFm.getTime();
        return new Date(msFm+ms);
    }
}
