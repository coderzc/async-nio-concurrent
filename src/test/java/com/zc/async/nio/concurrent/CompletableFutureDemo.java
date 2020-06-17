package com.zc.async.nio.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author coderzc
 * Created on 2020-06-12
 * 以 Async 结尾的都是提交至 asyncPool 执行，不带的如果准备执行时已经任务已经完成则使用用户线程执行，如果没有完成则使用提交至任务线程等待执行
 * whenComplete、handle 返回原始的CompletableFuture
 * thenApply、thenAccept、thenRun、 exceptionally 返回新的CompletableFuture (使用原始的CompletableFuture.get()/join() 不能阻塞住)
 */
public class CompletableFutureDemo {
    private long start;
    private static ExecutorService executor = Executors.newWorkStealingPool();

    private static final Logger logger = LoggerFactory.getLogger(CompletableFutureDemo.class);

    @Before
    public void before() {
        start = System.currentTimeMillis();
    }

    @After
    public void after() {
        long end = System.currentTimeMillis();
        logger.info("func run time: " + (end - start) + "ms");
    }

    @Test
    public void testGet() {
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchPrice, executor);
        try {
            Double aDouble = cf.get();
            logger.info("result:" + aDouble);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("err:", e);
        } finally {
            executor.shutdown();
        }
    }

    @Test // 抛出未检查异常
    public void testJoin() {
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchPrice, executor);
        try {
            Double aDouble = cf.join();
            logger.info("result:" + aDouble);
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void testWhenComplete() {
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchPrice, executor);
        try {
            cf.whenComplete((v, e) -> {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                logger.info("result:" + v);
            }).exceptionally(e -> {
                logger.error("err:", e);
                return 0.0;
            });

            CompletableFuture.allOf(cf).get();
        } catch (Exception e) {
            logger.error("err:", e);
        } finally {
            executor.shutdown();
        }
    }

    @Test // whenCompleteAsync 使用 asyncPool 执行
    public void testWhenCompleteAsync() {
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchPrice, executor);
        try {
            cf.whenCompleteAsync((v, e) -> {
                if (e != null) {
                    logger.error("err:", e);
                    return;
                }
                logger.info("result:" + v);
            });

            CompletableFuture.allOf(cf).get();
        } catch (Exception e) {
            logger.error("err:", e);
        } finally {
            executor.shutdown();
        }
    }

    @Test // handle 与 whenCompleteAsync 基本一致，可以对结果做修改
    public void testHandle() {
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchPrice, executor);
        try {
            cf.handle((v, e) -> {
                if (e != null) {
                    logger.error("err:", e);
                    return null;
                }
                logger.info("handle value:" + v);
                return v.byteValue() * 10;
            });

            CompletableFuture.allOf(cf).get();
        } catch (Exception e) {
            logger.error("err:", e);
        } finally {
            executor.shutdown();
        }
    }

    @Test // thenApply 会返回新的CompletableFuture 不能再从cf中获取
    public void testThenApply() {
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchPrice, executor);
        try {
            cf.thenApply(x -> {
                logger.info("thenApply:{}", x);
                return (int) x.doubleValue();
            }).thenApply(x -> {
                logger.info("thenApply2:{}", x);
                return (int) x.doubleValue() + 1;
            }).exceptionally(err -> {
                logger.error("err:", err);
                throw new RuntimeException("exceptionally catch err", err);
            }).get();

            System.out.println(cf.get());

        } catch (Exception e) {
            logger.error("err:", e);
        } finally {
            executor.shutdown();
        }
    }

    @Test // thenAccept 与 thenApply 基本一致 ，就是新的CompletableFuture 没有返回结果
    public void testThenAccept() {
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchPrice, executor);
        try {
            cf.thenAccept(x -> {
                logger.info("thenAccept:{}", x);
            }).join();

            CompletableFuture.allOf(cf).get();
        } catch (Exception e) {
            logger.error("err:", e);
        } finally {
            executor.shutdown();
        }
    }

    @Test // thenRun 与 thenAccept 与 thenApply 基本一致 ，就是没有入参数没有返回值
    public void testThenRun() {
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchPrice, executor);
        try {
            cf.thenRun(() -> {
                logger.info("thenRun:----");
            }).join();

            CompletableFuture.allOf(cf).get();
        } catch (Exception e) {
            logger.error("err:", e);
        } finally {
            executor.shutdown();
        }
    }

    public static Double fetchPrice() {
        try {
            logger.info("start fetchPrice");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error("err:", e);
            }
            if (Math.random() < 0.2) {
                throw new RuntimeException("fetch price failed!");
            }
            return 5 + Math.random() * 20;
        } finally {
            logger.info("end fetchPrice");
        }
    }
}
