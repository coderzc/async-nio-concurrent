package com.zc.async.nio.concurrent.synchronization;

/*
 * wait() notify() notifyAll() 生产者消费者
 */
public class WaitNotifyQueue {
    private static int size = 0; // 当前仓库容量
    private static final int MAX_SIZE = 5;

    //    private static final Integer objectLock = 1;
    private static final Object consumeLock = new Object();
    private static final Object produceLock = new Object();

    public static void consume() {
        synchronized (consumeLock) {
            while (size == 0) {
                System.out.println("消费者线程:" + Thread.currentThread().getName() + "仓库已空");
                try {
                    consumeLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("消费者线程:" + Thread.currentThread().getName() + " current size is: " + size);
            size--;
//            consumeLock.notifyAll();
        }

        synchronized (produceLock) {
            produceLock.notifyAll();//通知生产者
        }
    }


    public static void produce() {
        synchronized (produceLock) {
            while (size == MAX_SIZE) {
                System.out.println("生产者线程:" + Thread.currentThread().getName() + "仓库已满");
                try {
                    produceLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("生产者线程:" + Thread.currentThread().getName() + " current size is: " + size);
            size++;
//            produceLock.notifyAll();
        }

        synchronized (consumeLock) {
            consumeLock.notifyAll();// 通知消费者
        }
    }


    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                while (true) {
                    WaitNotifyQueue.produce();
                }
            }).start();
        }

        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                while (true) {
                    WaitNotifyQueue.consume();
                }
            }).start();
        }
    }

}
