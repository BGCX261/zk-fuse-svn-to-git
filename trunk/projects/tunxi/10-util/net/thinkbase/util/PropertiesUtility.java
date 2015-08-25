package net.thinkbase.util;

import java.util.* ;
import java.io.* ;
/**
 * 一组处理 Properties 的函数
 * @author thinkbase.net
 */
public class PropertiesUtility {
    /**获得系统的文件分隔符*/
    public static String getFileSeparator () {
        return System.getProperty("file.separator");
    }
    
    /**从一个文件加载属性到 Properties 对象*/
    public static Properties loadPropertiesFromFile (String sPath) throws IOException {
        //从文件加载属性
        Properties prop = new Properties();
        //读取app.properties中的各个属性
        FileInputStream in = new FileInputStream(sPath);  // 构造文件的输入流
        prop.load(in);           // 读入属性
        in.close();
        //返回
        return prop ;
    }
    
    /**从一个资源中加载属性到 Properties 对象*/
    public static Properties loadPropertiesFromResource (String resource) throws IOException {
        Properties prop = new Properties();
        InputStream is = PropertiesUtility.class.getResourceAsStream(resource);
        if (null!=is){
            prop.load(is);
            is.close();
        }
        return prop;
    }
}
