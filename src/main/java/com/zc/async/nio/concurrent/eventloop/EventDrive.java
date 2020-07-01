package com.zc.async.nio.concurrent.eventloop;

import static com.zc.async.nio.concurrent.eventloop.EventDrive.Event.INTERVAL_EVENT;
import static com.zc.async.nio.concurrent.eventloop.EventDrive.Event.KEY_EVENT;
import static com.zc.async.nio.concurrent.eventloop.EventDrive.Event.TIMER_EVENT;
import static com.zc.async.nio.concurrent.utils.LambdaUtils.catching;

import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author coderzc
 * Created on 2020-06-28
 */
public class EventDrive {
    private static final Logger logger = LoggerFactory.getLogger(EventDrive.class);
    private static final Set<Event> eventRegisterSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final BlockingQueue<CallBackEvent> callBackQueue = new LinkedBlockingDeque<>();

    static class CallBackEvent<T> extends Event<T> {

        public int keyCode;

        public CallBackEvent(int eventType, Runnable runnable) {
            this.eventType = eventType;
            this.runnable = runnable;
        }

        public CallBackEvent(int eventType, Consumer<T> consumer, int keyCode) {
            this.eventType = eventType;
            this.consumer = consumer;
            this.keyCode = keyCode;
        }
    }

    static class Event<T> {
        public static final int KEY_EVENT = 0;
        public static final int TIMER_EVENT = 1;
        public static final int INTERVAL_EVENT = 2;
        public int eventType;
        public long delay;
        public long eventInitTime;
        public Runnable runnable;
        public Consumer<T> consumer;

        public Event() {
        }

        public Event(int eventType, Consumer<T> consumer) {
            this.eventType = eventType;
            this.consumer = consumer;
        }

        public Event(int eventType, long delay, Runnable runnable) {
            this.eventType = eventType;
            this.delay = delay;
            this.eventInitTime = System.currentTimeMillis();
            this.runnable = runnable;
        }
    }

    // 主线程
    public static void main(String[] args) throws InterruptedException {
        // 启动事件循环线程
        new Thread(EventDrive::eventLoop).start();

        // 向事件监听器添加键盘事件
        eventRegisterSet.add(new EventDrive.Event<Integer>(KEY_EVENT, eventCode -> {
            logger.info("eventCode：" + eventCode);
            if (KeyEvent.VK_ENTER == eventCode) {
                logger.info("你点击了回车");
            } else if (KeyEvent.VK_SPACE == eventCode) {
                logger.info("你点击了空格");
                catching(() -> Thread.sleep(1000), Throwable::printStackTrace);
            }
        }));

        // 添加定时器事件
        eventRegisterSet.add(new EventDrive.Event<>(TIMER_EVENT, 1000L * 30, () -> {
            logger.info("你的定时器执行了");
        }));

        // 添加一个周期事件
        eventRegisterSet.add(new EventDrive.Event<>(INTERVAL_EVENT, 1500L, () -> {
            logger.info("你的周期定时器执行了");
        }));

        // 初始化DOM。。。

        // 从事件队列里拿出准备好的事件，执行回调函数
        while (true) {
            CallBackEvent<?> callbackEvent = callBackQueue.take();
            if (callbackEvent.eventType == KEY_EVENT) {
                // 键盘事件
                ((CallBackEvent<Integer>) callbackEvent).consumer.accept(callbackEvent.keyCode);
            } else if (callbackEvent.eventType == TIMER_EVENT || callbackEvent.eventType == INTERVAL_EVENT) {
                // 定时器事件
                callbackEvent.runnable.run();
            }

        }
    }

    // 事件线程
    public static void eventLoop() {
        while (true) {
            catching(() -> {
                // 遍历事件注册集合，找到注册了该按键的事件把它的回调函数放到事件就绪队列中，等待主线程执行
                Iterator<Event> iterator = eventRegisterSet.iterator();
                while (iterator.hasNext()) {
                    Event event = iterator.next();
                    switch (event.eventType) {
                        // 获取键盘输入，等待用户按下，轮询检测，非阻塞
                        case KEY_EVENT:
                            if (System.in.available() != 0) {
                                int keyCode = System.in.read();
                                callBackQueue.put(new CallBackEvent<Integer>(KEY_EVENT, event.consumer, keyCode));
                            }
                            break;
                        // 延迟器事件
                        case TIMER_EVENT:
                            if (System.currentTimeMillis() - event.eventInitTime >= event.delay) {
                                callBackQueue.put(new CallBackEvent<>(TIMER_EVENT, event.runnable));
                                iterator.remove();
                            }
                            break;
                        // 周期定时器事件
                        case INTERVAL_EVENT:
                            if (System.currentTimeMillis() - event.eventInitTime >= event.delay) {
                                callBackQueue.put(new CallBackEvent<>(TIMER_EVENT, event.runnable));
                                event.eventInitTime = System.currentTimeMillis();// 重置时间
                            }
                            break;
                        default:
                    }
                }
            }, Throwable::printStackTrace);
        }
    }
}
