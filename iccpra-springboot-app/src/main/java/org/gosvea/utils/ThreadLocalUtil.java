package org.gosvea.utils;

public class ThreadLocalUtil {
    //提供threadlocal对象
    private static final ThreadLocal THREAD_LOCAL=new ThreadLocal();

    //get

    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }

    //存储键值对
    public static void set(Object value){
        THREAD_LOCAL.set(value);

    }
    //释放内存
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
