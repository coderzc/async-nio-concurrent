package com.zc.async.nio.concurrent.async;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.Futures.FutureCombiner;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * @author coderzc
 * Created on 2020-06-22
 */
public class ListenableFutureDemo {
    private static final Logger logger = LoggerFactory.getLogger(ListenableFutureDemo.class);
    private static final ListeningExecutorService service =
            MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    public void testAddCallback() {
        ListenableFuture<String> future = service.submit(() -> UUID.randomUUID().toString());
        Futures.addCallback(future, new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String s) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(s + ":success");
            }

            @Override
            public void onFailure(Throwable throwable) {
                logger.error("err:", throwable);
            }
        }, Runnable::run);

        service.shutdown();
    }

    public void testAllComplete() {
        FutureCombiner<String> stringFutureCombiner = Futures.whenAllComplete(
                service.submit(this::asyncTask),
                service.submit(this::asyncTask),
                service.submit(this::asyncTask));
        stringFutureCombiner.run(() -> {
            System.out.println("all over");
        }, Runnable::run);

        System.out.println("123");
        service.shutdown();
    }

    public void testAllAsList() throws ExecutionException, InterruptedException {
        ListenableFuture<List<String>> listListenableFuture = Futures.allAsList(
                service.submit(this::asyncTask),
                service.submit(this::asyncTask),
                service.submit(this::asyncTask));
        listListenableFuture.get();
        System.out.println("123");
        service.shutdown();
    }

    public String asyncTask() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String string = UUID.randomUUID().toString();
        logger.info(string);
        return string;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ListenableFutureDemo listenableFutureDemo = new ListenableFutureDemo();
//        listenableFutureDemo.testAddCallback();
//        listenableFutureDemo.testAllComplete();
        listenableFutureDemo.testAllAsList();
    }

}
