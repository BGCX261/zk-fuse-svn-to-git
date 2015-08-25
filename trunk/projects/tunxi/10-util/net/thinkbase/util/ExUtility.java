package net.thinkbase.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author thinkbase.net
 *
 * ExUtility : 用于处理Exception的通用辅助类
 */
public class ExUtility {
    /** Get the stack trace of an Exception */
    public static String getStackTrace(Throwable ex) {
        String sStack = "";
        try {
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            ex.printStackTrace(pw);
            sStack = sw.toString();
            pw.close();
            sw.close();
        }
        catch (Exception e) {
            sStack = "Can't get StackTrace, reason:"+
                     "["+e.getClass().getName()+"; "+e.getMessage()+"]";
        }
        return sStack;
    }
    
    /** Show the String of an Exception */
    public static String toString(Throwable ex) {
        if (ex instanceof java.sql.SQLException){
            return _SQLEx2String((java.sql.SQLException)ex);
        }
        else {
            return "[Exception Name:"+ex.getClass().getName()+"];\n"+
                   "[Error Message:"+ex.getMessage()+"];\n"+
                   "[StackTrace:"+ExUtility.getStackTrace(ex)+"].\n";
        }
    }
    /** Show the String of an SQLException */
    private static String _SQLEx2String(java.sql.SQLException ex) {
        String s =
               "[Exception Name:"+ex.getClass().getName()+"];\n"+
               "[Error Message:"+ex.getMessage()+"];\n"+
               "[Error Code:"+Integer.toString(ex.getErrorCode())+"];\n"+
               "[SQLState:"+ex.getSQLState()+"];\n"+
               "[StackTrace:"+ExUtility.getStackTrace(ex)+"].\n";
        java.sql.SQLException nex = ex.getNextException();
        if (null!=nex){
            s += "NextException --------------------------------------------------\n";
            s += _SQLEx2String(nex);
        }
        return s;
    }

    //获得某个 Exception 的 cause Exception, 返回 null 代表此 Exception 没有 cause 了
    public static Throwable getCause(Throwable t){
    	try{
    		throw t;
    	}catch(UndeclaredThrowableException e){
    		if (null!=e.getUndeclaredThrowable()){
    			return e.getUndeclaredThrowable();
    		}else{
    			return e.getCause();
    		}
    	}catch(InvocationTargetException e){
    		return e.getTargetException();
    	}catch(Throwable rt){
    		Throwable c = rt.getCause();
    		if (null!=c){
    			return c;
    		}else{
    			return rt;
    		}
    	}
    }
    /**
     * 按照不同的情况, 抛出 RuntimeException, 同时避免太多层次的 RuntimeException
     * @param t 异常对象, 包括 Exception, Error 等
     * @param appendMessage 附加在异常对象上的消息
     */
    public static void throwRuntimeException(Throwable t, String appendMessage){
		Throwable cause = getCause(t);
		if (null==cause){
    		if (t instanceof RuntimeException){
    			throw (RuntimeException)t;
    		}else if (t instanceof Error){
    			throw (Error)t;
    		}else{
    			throw new RuntimeException(appendMessage, t);
    		}
		}else{
			throwRuntimeException(cause, appendMessage);
		}
    }
    /**
     * 按照不同的情况, 抛出 RuntimeException, 同时避免太多层次的 RuntimeException
     * @param t 异常对象, 包括 Exception, Error 等
     */
    public static void throwRuntimeException(Throwable t){
    	throwRuntimeException(t, null);
    }
}
