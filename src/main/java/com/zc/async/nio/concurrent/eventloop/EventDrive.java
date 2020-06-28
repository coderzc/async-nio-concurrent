package com.zc.async.nio.concurrent.eventloop;

import static com.zc.async.nio.concurrent.eventloop.EventDrive.Event.KEY_EVENT;
import static com.zc.async.nio.concurrent.eventloop.EventDrive.Event.TIMER_EVENT;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author coderzc
 * Created on 2020-06-28
 */
public class EventDrive {
    private static final Set<Event> eventRegisterSet = new HashSet<>();
    private static final BlockingQueue<Runnable> eventQueue = new LinkedBlockingDeque<>();

    static class Event {
        public static final int KEY_EVENT = 0;
        public static final int TIMER_EVENT = 1;
        int eventType;
        long delay;
        long eventInitTime;
        int keyEventCode;
        Runnable callable;
        public Event(int eventType, int keyEventCode, Runnable callable) {
            this.eventType = eventType;
            this.keyEventCode = keyEventCode;
            this.callable = callable;
        }
        public Event(int eventType, long delay, Runnable callable) {
            this.eventType = eventType;
            this.delay = delay;
            this.eventInitTime = System.currentTimeMillis();
            this.callable = callable;
        }
    }

    // 主线程
    public static void main(String[] args) throws InterruptedException {
        // 启动事件循环线程
        new Thread(EventDrive::eventLoop).start();

        // 向事件监听器添加键盘回车事件
        eventRegisterSet.add(new EventDrive.Event(KEY_EVENT, KeyEvent.VK_ENTER, () -> {
            System.out.println("你点击了回车");
        }));

        // 添加定时器事件
        eventRegisterSet.add(new EventDrive.Event(TIMER_EVENT, 1000L, () -> {
            System.out.println("你的定时器执行了");
        }));

        // 初始化DOM。。。

        // 从事件队列里拿出准备好的事件，执行回调函数
        while (true) {
            Runnable callback = eventQueue.take();
            callback.run();
        }
    }

    // 事件线程
    public static void eventLoop() {
        while (true) {
            try {
                // 遍历事件注册集合，找到注册了该按键的事件把它的回调函数放到事件就绪队列中，等待主线程执行
                for (Event event : eventRegisterSet) {
                    if (event.eventType == KEY_EVENT) {
                        // 获取键盘输入，等待用户按下，阻塞中。。。
                        int keyCode = System.in.read();
                        if (event.keyEventCode == keyCode) {
                            eventQueue.put(event.callable);
                        }
                    } else if (event.eventType == TIMER_EVENT) {
                        // 定时器事件
                        if(System.currentTimeMillis() - event.eventInitTime >=event.delay){
                            eventQueue.put(event.callable);
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
