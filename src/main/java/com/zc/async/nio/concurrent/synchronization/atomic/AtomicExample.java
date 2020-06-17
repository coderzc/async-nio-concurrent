package com.zc.async.nio.concurrent.synchronization.atomic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import com.zc.async.nio.concurrent.annoation.ThreadSafe;

/**
 * @author: coderzc
 */
@Slf4j
@ThreadSafe
public class AtomicExample {

    //请求数
    public static int clientTotal = 5000;

    //同时并发线程数
    public static int threadTotal = 50;

    //共享变量
    public static AtomicInteger count = new AtomicInteger(0);


    private static void add() {
        //i++;
        count.getAndIncrement();
        //++i;
//        count.incrementAndGet();
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);

        for (int i = 0; i < clientTotal; i++) {
            executorService.execute(() -> {
                try {
                    //用信号量控制线程并发量，如果当前线程已经达到最大则先阻塞新线程
                    semaphore.acquire();
                    add();
                    semaphore.release();
                } catch (Exception e) {
                    log.error("exception", e);
                }
                countDownLatch.countDown();
            });
        }

        //计数器为零，所有线程执行完成
        countDownLatch.await();
        log.info("count:{}",count.get());
        //释放线程池
        executorService.shutdown();
    }
}
