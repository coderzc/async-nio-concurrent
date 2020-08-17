package com.zc.async.nio.concurrent.async;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaocong <zhaocong@kuaishou.com>
 * Created on 2020-08-06
 */
public class TimerDemo {
    public static void main(String[] args) {
        // 定时器
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("task run");
            }
        }, 2000);

        // sleep
        new Thread(()->{
            try {
                Thread.sleep(2000);
                System.out.println("sleep run");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // ScheduledExecutorService
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(()->{
            System.out.println("ScheduledExecutorService run");
        },2, TimeUnit.SECONDS);

    }
}
