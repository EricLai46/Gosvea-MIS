package org.gosvea;

import org.junit.Test;

public class ThreadLocalTest {

    @Test
    public void testThreadLocalSetAndGet(){
            ThreadLocal t1=new ThreadLocal<>();


            new Thread().start();
    }
}
