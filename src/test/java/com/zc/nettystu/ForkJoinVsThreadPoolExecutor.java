package com.zc.nettystu;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.util.DigestUtils;

/**
 * @author zhaocong <zhaocong@kuaishou.com>
 * Created on 2020-06-17
 */
public class ForkJoinVsThreadPoolExecutor {

    @Test
    public void testForkJoin() {
        /**
         * this(Math.min(MAX_CAP, Runtime.getRuntime().availableProcessors()),
         *              defaultForkJoinWorkerThreadFactory, null, false,
         *              0, MAX_CAP, 1, null, DEFAULT_KEEPALIVE, TimeUnit.MILLISECONDS);
         */
        ForkJoinPool forkJoinPool = new ForkJoinPool(2);
        forkJoinPool.execute(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "---> start run 1");
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName() + "---> 1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        forkJoinPool.execute(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "---> start run 2");
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName() + "---> 2");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        forkJoinPool.execute(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "---> start run 3");
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName() + "---> 3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        forkJoinPool.execute(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "---> start run 4");
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName() + "---> 4");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        forkJoinPool.execute(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "---> start run 5");
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName() + "---> 5");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        while (!forkJoinPool.isQuiescent()) {
        }
    }

    @Test
    public void forkJoin2ExecutorService() throws InterruptedException, ExecutionException {
        /**
         * return new ForkJoinPool
         *             (Runtime.getRuntime().availableProcessors(),
         *              ForkJoinPool.defaultForkJoinWorkerThreadFactory,
         *              null, true);
         */
        long startTime = System.currentTimeMillis();
        //        ExecutorService service = Executors.newWorkStealingPool();
        ExecutorService service = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(), 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
        List<Future<?>> list = new ArrayList<>();

        for (int i = 0; i < 1000000; i++) {
            try {
                int finalI = i;
                Future<?> submit = service.submit(() -> {
                    String code = DigestUtils
                            .md5DigestAsHex(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()));
                    System.out.println(Thread.currentThread().getName() + "---> " + finalI + "md5:" + code);
                });
                list.add(submit);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        for (Future<?> future : list) {
            future.get();
        }

        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}
