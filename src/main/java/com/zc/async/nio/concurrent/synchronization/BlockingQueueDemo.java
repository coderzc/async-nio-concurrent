package com.zc.async.nio.concurrent.synchronization;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueDemo {
    private static BlockingQueue<Object> queue = new ArrayBlockingQueue<>(5);

    public static void produce() {
        queue.add(new Object());
        System.out.println("线程:" + Thread.currentThread().getName() + " 生产 size is: " + queue.size());
    }

    public static Object consume() throws InterruptedException {
        Object take = queue.take();
        System.out.println("线程:" + Thread.currentThread().getName() + " 消费 size is: " + queue.size());
        return take;
    }

    public static void main(String[] args) {

    }
}
