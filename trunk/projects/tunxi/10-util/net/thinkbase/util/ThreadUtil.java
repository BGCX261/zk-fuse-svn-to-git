package net.thinkbase.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程操作相关的类
 * @author thinkbase.net
 */
public class ThreadUtil {
    /**
     * Block 当前线程, 等待获得需要的对象, 直到超时
     * @param provider 提供需要返回的对象
     * @param timeoutMs 超时时间(毫秒), 等于 0 则没有超时
     * @return
     * @throws InterruptedException
     */
    public static Object waitingObjectReady(final IObjectProvider provider, long timeoutMs) throws InterruptedException{
        final ReentrantLock lock = new ReentrantLock();
        final Object[] objectBox = new Object[]{null, null}; //[0]:返回值, [1]:线程开始标记
        Thread thread = new Thread(new Runnable(){
            public void run() {
                try{
                    lock.lock();
                    objectBox[1] = "ThreadStarted";  //获得锁后标记线程已开始运行
                    while(null==objectBox[0]){
                        objectBox[0] = provider.getObject();
                    }
                }finally{
                    lock.unlock();
                }
            }
        },"Thread: Waiting " +provider.getName());
        thread.start();
        while(null==objectBox[1]){  //等待线程开始(让线程先获得锁)
          Thread.sleep(10);
        }
        if (null!=objectBox[0]){
            return objectBox[0];
        }
        boolean locked = false;
        try{
            if (timeoutMs > 0){
                locked = lock.tryLock(timeoutMs, TimeUnit.MILLISECONDS);
            }else{
                lock.lock();
                locked = true;
            }
            if (! locked){
                throw new InterruptedException(
                        "Waiting "+provider.getName()+" Timeout("+timeoutMs+" milliseconds) !");
            }
        }finally{
            if (locked) lock.unlock();
        }
        return objectBox[0];
    }
    public static interface IObjectProvider{
        /**获取所需的对象, 返回 null 说明对象还没有就绪*/
        public Object getObject();
        /**对所需对象的说明*/
        public String getName();
    }
}
