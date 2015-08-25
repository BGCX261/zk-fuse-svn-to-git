package net.thinkbase.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 提供一些字符串处理的工具
 * @author thinkbase.net
 */
public class StringUtility {
    
    /**
     * 将两部分字符串组合起来, 考虑连接字符串不能重复的情况
     */
    static public String joinString (String str1, String str2, String sSplit) {
        String s1 = str1;
        String s2 = str2;
        int iLen = sSplit.length();
        if (iLen>0) {
            if (s1.length()>=iLen) {
                String sTail = s1.substring(s1.length()-iLen);
                if (sSplit.equals(sTail)) s1 = s1.substring(0,s1.length()-iLen) ;
            }
            if (s2.length()>=iLen) {
                String sHead = s2.substring(0,iLen);
                if (sSplit.equals(sHead)) s2 = s2.substring(iLen);
            }
        }
        return (s1+sSplit+s2) ;
    }
    
    /**
     * Join a string array with splitter
     * @param strs
     * @param splitter
     * @return
     */
    public static String joinArray(String[] strs, String splitter){
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < strs.length; i++) {
            if (i>0){
                buf.append(splitter);
            }
            buf.append(strs[i]);
        }
        return buf.toString();
    }
    
    /**
     * 类似String类型的replace, 但寻找和替换的对象是字符串(JDK1.4有类似功能)
     */
    static public String replace (String sSrc, String sFind, String sRept) {
        String sRep = sRept;
        //一些检查
        if (sSrc==null || sSrc.length()==0) return (sSrc);
        if (sFind==null||sFind.length()==0) return (sSrc);
        if (sRep==null) sRep = "" ;
        //开始替换
        int iStart = 0,iEnd = 0;
        int iFindLen = sFind.length();
        StringBuffer buf = new StringBuffer();
        while ((iEnd = sSrc.indexOf(sFind, iStart)) >= 0) {
            buf.append(sSrc.substring(iStart, iEnd));
            buf.append(sRep);
            iStart = iEnd + iFindLen;
        }
        buf.append(sSrc.substring(iStart));
        return buf.toString();
    }

    /**
     * 对字符串进行 URL 编码<br>
     * @return 经过 URL 编码的字符串
     * @param sIn 输入的字符串
     * @param encoding 字符串编码方式, 例如 GBK, UTF-8 等等
     * @throws UnsupportedEncodingException 可能引起的异常
     */
    public static String urlEncode(String sIn, String encoding) throws java.io.UnsupportedEncodingException {
        //如果输入空串, 直接返回
        if (sIn==null || sIn.length()==0){return sIn;}
        //将字符串转储到一个字节数组中
        byte[] byteIn = sIn.getBytes(encoding);
        //开始处理数据 ...
        StringBuffer buf=new StringBuffer();
        int iU=byteIn.length ;
        String sChar ; int iChar;
        for (int i=0;i<iU;i++){
            //获得一个字节
            iChar = (int)byteIn[i];
            //a-z, A-Z, 0-9, '.' '-' '*' '_' 保持不变, 空格变为 + , 其它则用 %xy 形式表示
            if ( (iChar >= 0x30 && iChar <= 0x39) || //30 - '0', 39 - '9'
                 (iChar >= 0x41 && iChar <= 0x5A) || //41 - 'A', 5A - 'Z'
                 (iChar >= 0x61 && iChar <= 0x7A) || //61 - 'a', 7A - 'z'
                 //2A:'*', 2E:'.', 2D:'-', 5F:'_'
                 (iChar==0x2A)||(iChar==0x2E)||(iChar==0x2D)||(iChar==0x5F) )
            {
                sChar=String.valueOf( (char)iChar ) ;
            }
            else if (iChar == 0x20)  //0x20:空格
            {
                sChar="+";
            }
            else
            {
                //iChar小于0表示是一个大于127的ASCII码
                if (iChar<0){iChar=iChar+256;}
                //获得对应的二进制表示
                sChar = Integer.toHexString(iChar).toUpperCase() ;
                //sChar小于两位说明是诸如 Tab, 回车等控制字符
                if (sChar.length()<2) {sChar="0"+sChar;}
                //前面加上 %
                sChar = "%" + sChar ;
            }
            buf.append(sChar );
        }
        return buf.toString();
    }
    
    /**
     * 替换输入字符串中的非法 HTML 标记
     * @param sIn 输入的字符串
     * @return 经过处理后的字符串
     */
    public static String escapeHTMLTag(String sIn){
        //如果输入空串, 直接返回
        if (sIn==null || sIn.length()==0){return sIn;}
        //开始处理数据 ...
        StringBuffer buf=new StringBuffer();
        char ch=' ';
        int iU=sIn.length();
        for (int i=0;i<iU;i++){
            ch=sIn.charAt(i);
            if (ch=='<') {buf.append("&lt;");}
            else if (ch=='>') {buf.append("&gt;");}
            else if (ch=='\n') {buf.append("<br>");}
            //IE6.0.2600英文版中, 如果 input type="text" value="...&nbsp;...", 显示的仍然是个空格, 但 post 到后台的却是 &nbsp;
            //else if (ch==' ') {buf.append("&nbsp;");}
            else if (ch=='\'') {buf.append("&acute;");}
            else if (ch=='\"') {buf.append("&quot;");}
            else if (ch=='&') {buf.append("&amp;");}
            else {buf.append(ch);}
        }
        return buf.toString();
    }
    
    /**
     * 为输入字符进行 xml 编码
     * @param sIn
     * @return
     */
    public static String escapeXml(String sIn){
        //如果输入空串, 直接返回
        if (sIn==null || sIn.length()==0){return sIn;}
        //开始处理数据 ...
        StringBuffer buf=new StringBuffer();
        char ch=' ';
        int iU=sIn.length();
        for (int i=0;i<iU;i++){
            ch=sIn.charAt(i);
            if (ch=='<') {buf.append("&lt;");}
            else if (ch=='>') {buf.append("&gt;");}
            else if (ch=='\'') {buf.append("&acute;");}
            else if (ch=='\"') {buf.append("&quot;");}
            else if (ch=='&') {buf.append("&amp;");}
            else {buf.append(ch);}
        }
        return buf.toString();
    }
    
    /**
     * 替换输入字符串中的"'",以便用于SQL语句中
     * @param sIn 输入的字符串
     * @return 经过处理后的字符串
     */
    public static String escapeSQL(String sIn) {
        //如果输入空串, 直接返回
        if (sIn==null || sIn.length()==0){return sIn;}
        //开始处理数据 ...
        StringBuffer buf=new StringBuffer();
        char ch=' ';
        int iU=sIn.length();
        for (int i=0;i<iU;i++){
            ch=sIn.charAt(i);
            if (ch=='\'') {
                buf.append("\'\'");
            }
            else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }
    
    /**
     * 将字符串按照分割符拆分
     * @param sIn 需要分割的字符串, 不能是 null
     * @param splitter 用于分割的字符串
     * @return 分割后的字符串数组, 如果 sIn 中不包括 splitter, 返回的数组只有一个元素 sIn
     */
    @SuppressWarnings("unchecked")
    static public String[] split(String sIn, String splitter){
        List lst = new ArrayList();
        StringTokenizer st = new StringTokenizer(sIn, splitter);
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            lst.add(s);
        }
        //Store List to String Array
        int len = lst.size();
        String[] res = new String[len];
        for (int i=0; i<len; i++){
            res[i] = (String) lst.get(i);
        }
        //Return
        return res;
    }
    
    /**
     * 按照最大长度截取字符串
     * @param str 需要截取的字符串
     * @param maxLength 需要截取的字符串允许的最大长度
     * @param mask 标志字符串是否经过截取的符合, 例如 " ..." 等
     * @return 截取后的字符串, 其最大长度不会超过 maxLength
     * <br>注意:如果需要截取的字符串是 null, 则照样返回 null
     */
    static public String truncate (String str, int maxLength, String mask){
        if (null==str) return str;
        if ("".equals(str)) return str;
        if (maxLength <=0) return str;
        
        String tail = mask;
        int len = str.length();
        if (null==mask){
            if (len > maxLength) len=maxLength;
            tail="";
        }else{
            if (len > (maxLength-mask.length())) len=maxLength-mask.length();
        }
        String head = str.substring(0, len);
        if (str.length() != head.length()) head += tail;
        
        return head;
    }
    
    /**
     * SSV (Semicolon Separated Values,分号分割的Properties数据)的解析器
     * SSV 格式:1)各个项目之间用分号分割;
                2)项目表示为 Key=Value的形式, Key不包括头尾的空格;
                3)ASCII escape: 
                    1>回车将被忽略, 2>"\n"=回车, 3>"\;"=分号, 4>"\\"=转义符, 5>"\="=等于号
     * 说明 : 如果一个 SSV 项目没有 "=" 或者 "=" 处于最后, 认为 Value = "".
     */
    static public Properties ssv2Properties(String ssvString){
        /**处理思路
         * 1>清除所有的回车(\n,\r)
         * 2>\; 被替换成\n, \= 被替换成\r
         * 3>用分号解析开字符串
         * 4>对解析开的每一项:
         *    按照 "=" 拆分为 Key 与 Value
         *    对Key与Value:回车(\n)替换回分号, 回车(\r)替换回等号, \n -> 回车, \\ -> \
         *    去除Key头尾的空格
         */
        String ssv = ssvString;
        //1>清除所有回车
        ssv = replace(ssv, "\n", "");
        ssv = replace(ssv, "\r", "");
        //2>\; 被替换成\n, \= 被替换成\r
        ssv = replace(ssv, "\\;", "\n");
        ssv = replace(ssv, "\\=", "\r");
        //3>用分号解析
        Properties p = new Properties();
        StringTokenizer st = new StringTokenizer(ssv, ";");
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            //按照 "=" 拆分为 Key 与 Value
            String sKey = ""; String sVal = "";
            int iFirst = s.indexOf("=");
            if (-1==iFirst){    //字符串中没有"="
                //认为只有Key, 没有 Value
                sKey = s ;
            }
            else {
                sKey = s.substring(0, iFirst);
                sVal = s.substring(iFirst+1);   //即使"="在字符串最后也是安全的
            }
            //对Key与Value:回车(\n)替换回分号, 回车(\r)替换回等号, \n -> 回车, \\ -> \
            sKey = replace(sKey, "\n",";");sVal = replace(sVal, "\n",";");
            sKey = replace(sKey, "\r","=");sVal = replace(sVal, "\r","=");
            sKey = replace(sKey, "\\n","\n");sVal = replace(sVal, "\\n","\n");
            sKey = replace(sKey, "\\\\","\\");sVal = replace(sVal, "\\\\","\\");
            sKey = sKey.trim();
            //加入 Properties
            p.setProperty(sKey, sVal);
        }
        //返回
        return p;
    }
    
    /**
     * Extract substring with regex
     * @param source
     * @param regex
     * @return
     */
    public static String[] extract(String source, String regex){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(source);
        if (m.matches()){
            int count = m.groupCount();
            String[] res = new String[count];
            for (int i = 0; i < count; i++) {
                res[i] = m.group(i+1);
            }
            return res;
        }else{
            return new String[]{};
        }
    }
    
    /**
     * Extract substring between gaps
     * @param source
     * @param gaps
     * @return
     */
    public static String[] extract(String source, String[] gaps){
        String regex = joinArray(gaps, "(.*)");
        return extract(source, regex);
    }
}
