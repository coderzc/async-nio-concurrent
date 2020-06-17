package com.zc.async.nio.concurrent.synchronization;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedToLock {
    private Lock lock = new ReentrantLock();


    public synchronized void method1() {
        System.out.println("我是synchronized锁");
    }

    public void method2() {
        lock.lock();
        try {
            System.out.println("我是lock锁");
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        SynchronizedToLock synchronizedToLock = new SynchronizedToLock();
        synchronizedToLock.method1();
//        synchronizedToLock.method2();

    }
}
