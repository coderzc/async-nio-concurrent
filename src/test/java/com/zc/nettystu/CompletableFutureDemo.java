package com.zc.nettystu;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * @author coderzc
 * Created on 2020-06-12
 */
public class CompletableFutureDemo {
    private static CountDownLatch countDownLatch  = new CountDownLatch(1);
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 创建异步执行任务:
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchPrice);

        cf.thenAccept(res -> {
            System.out.println("price: " + res);
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });

        CompletableFuture.allOf(cf).get();

    }

    public static Double fetchPrice() {
        try {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            if (Math.random() < 0.3) {
                throw new RuntimeException("fetch price failed!");
            }
            return 5 + Math.random() * 20;
        }finally {
            countDownLatch.countDown();
        }
    }
}
