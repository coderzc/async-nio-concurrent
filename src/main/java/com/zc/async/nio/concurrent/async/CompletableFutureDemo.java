package com.zc.async.nio.concurrent.async;

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
 * 创建无返回异步任务
 * runAsync
 *
 * 创建有返回异步任务
 * supplyAsync
 *
 * 异步任务正常执行完或者抛出异常时
 * whenComplete
 * whenCompleteAsync
 *
 * 异步任务抛出异常时
 * exceptionally
 *
 * 异步任务串行化,前一个有返回异步任务正常执行完,返回值作为下一个有参有返回异步任务的参数
 * thenApply
 * thenApplyAysnc
 *
 * 异步任务串行化,前一个有返回异步任务正常执行完或者抛出异常时,返回值作为下一个有参有返回异步任务的参数
 * handle
 * handleAsync
 *
 * 异步任务串行化,前一个有返回异步任务正常执行完,返回值作为下一个有参无返回异步任务的参数
 * thenAccept
 * thenAcceptAsync
 *
 * 异步任务串行化,前一个异步任务正常执行完,执行下一个无参无返回的异步任务
 * thenRun
 * thenRunAsync
 *
 * 整合异步任务,两个异步任务都执行完,把两个异步任务的结果放到一块处理, 有参有返回
 * thenCombine
 * thenCombineAsync
 *
 * 整合异步任务,两个异步任务都执行完,把两个异步任务的结果放到一块处理, 有参无返回
 * thenAcceptBoth
 * thenAcceptBothAsync
 *
 * 整合异步任务,哪个返回结果快就使用哪个结果,有参有返回
 * applyToEither
 * applyToEitherAsync
 *
 * 整合异步任务,哪个返回结果快就使用哪个结果,有参无返回
 * acceptEither
 * acceptEitherAsync
 *
 * 两个异步任务,任何一个执行完成了都会执行下一步操作,无参无返回
 * runAfterEither
 * runAfterEitherAsync
 *
 * 两个异步任务,都完成了才会执行下一步操作,无参无返回
 * runAfterBoth
 * runAfterBothAsync
 *
 * 所有的异步任务都完成
 * allOf
 *
 * 任意一个异步任务完成
 * anyOf
 *
 * 获取异步任务结果
 * get
 * join
 * 区别:get会抛异常, join不会抛异常
 *
 * 以 Async 结尾的都是提交至 asyncPool 执行，不带的如果准备执行时已经任务已经完成则使用用户线程执行，如果没有完成则使用提交至任务线程等待执行
 * whenComplete(BiConsumer <value,err>)、handle(BiFunction<value,err><value>)、thenRun(Runnable)、exceptionally（但是依然会抛出异常） 返回相同的CompletableFuture
 * thenApply(Function<value><valueNew>)、thenAccept(Consumer<value><Void>) 返回不同类型的CompletableFuture (使用原始的CompletableFuture.get()/join() 不能阻塞住)
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
    public void testExceptionally(){
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchPrice, executor);
        try {
            cf.exceptionally(e -> {
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
            });

            cf.get();
//            CompletableFuture.allOf(cf).get();
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
            });

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
            if (Math.random() < 0.9) {
                throw new RuntimeException("fetch price failed!");
            }
            return 5 + Math.random() * 20;
        } finally {
            logger.info("end fetchPrice");
        }
    }
}
