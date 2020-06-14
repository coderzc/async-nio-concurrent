package com.zc.nettystu;

import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author coderzc
 * Created on 2020-06-12
 */
public class CompletableFutureDemo {
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
//        forkJoin2ExecutorService();

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
                e.printStackTrace();
            }
            if (Math.random() < 0.3) {
                throw new RuntimeException("fetch price failed!");
            }
            return 5 + Math.random() * 20;
        } finally {
            countDownLatch.countDown();
        }
    }

    public static void forkJoin2ExecutorService() throws InterruptedException, ExecutionException {
        /**
         * this(Math.min(MAX_CAP, Runtime.getRuntime().availableProcessors()),
         *              defaultForkJoinWorkerThreadFactory, null, false,
         *              0, MAX_CAP, 1, null, DEFAULT_KEEPALIVE, TimeUnit.MILLISECONDS);
         */
//        ForkJoinPool forkJoinPool = new ForkJoinPool(2);
//        forkJoinPool.execute(() -> {
//            try {
//                System.out.println(Thread.currentThread().getName() + "---> start run 1");
//                Thread.sleep(3000);
//                System.out.println(Thread.currentThread().getName() + "---> 1");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//
//
//        forkJoinPool.execute(() -> {
//            try {
//                System.out.println(Thread.currentThread().getName() + "---> start run 2");
//                Thread.sleep(3000);
//                System.out.println(Thread.currentThread().getName() + "---> 2");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//
//
//        forkJoinPool.execute(() -> {
//            try {
//                System.out.println(Thread.currentThread().getName() + "---> start run 3");
//                Thread.sleep(3000);
//                System.out.println(Thread.currentThread().getName() + "---> 3");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//
//
//        forkJoinPool.execute(() -> {
//            try {
//                System.out.println(Thread.currentThread().getName() + "---> start run 4");
//                Thread.sleep(3000);
//                System.out.println(Thread.currentThread().getName() + "---> 4");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//
//        forkJoinPool.execute(() -> {
//            try {
//                System.out.println(Thread.currentThread().getName() + "---> start run 5");
//                Thread.sleep(3000);
//                System.out.println(Thread.currentThread().getName() + "---> 5");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//
//        while (!forkJoinPool.isQuiescent()) {
//        }

        /**
         * return new ForkJoinPool
         *             (Runtime.getRuntime().availableProcessors(),
         *              ForkJoinPool.defaultForkJoinWorkerThreadFactory,
         *              null, true);
         */
        long startTime = System.currentTimeMillis();
//        ExecutorService service = Executors.newWorkStealingPool();
        ExecutorService service = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
        List<Future<?>> list = new ArrayList<>();

        for (int i = 0; i < 1000000; i++) {
            try {
                int finalI = i;
                Future<?> submit = service.submit(() -> {
                    String code = DigestUtils.md5DigestAsHex(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()));
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
