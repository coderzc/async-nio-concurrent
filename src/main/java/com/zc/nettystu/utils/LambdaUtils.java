package com.zc.nettystu.utils;

import java.util.function.Consumer;

/**
 * @author coderzc
 * Created on 2020-06-12
 */
public class LambdaUtils {
    public interface RunnableThrow {
        void run() throws Throwable;
    }
    public static void catching(RunnableThrow runnable, Consumer<? super Throwable> exceptionConsumer){
        try {
            runnable.run();
        }catch (Throwable throwable){
            exceptionConsumer.accept(throwable);
        }
    }

}
