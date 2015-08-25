package net.thinkbase.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * 处理文本文件的工具
 * @author thinkbase.net
 */
public class TextFileUtil {
    public static final String readAsText(InputStream input, String charset) throws IOException{
        Reader rd;
        if (null==charset){
            rd = new InputStreamReader(input);
        }else{
            rd = new InputStreamReader(input, charset);
        }
        StringBuffer buf = new StringBuffer();
        int c = rd.read();
        while(-1!=c){
            buf.append((char)c);
            c = rd.read();
        }
        rd.close();
        return buf.toString();
    }
    public static final String readAsText(InputStream input) throws IOException{
        return readAsText(input, null);
    }
}
