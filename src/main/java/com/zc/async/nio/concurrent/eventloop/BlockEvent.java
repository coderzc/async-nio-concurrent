package com.zc.async.nio.concurrent.eventloop;

import static com.zc.async.nio.concurrent.eventloop.EventLoop.Event.KEY_EVENT;
import static com.zc.async.nio.concurrent.utils.LambdaUtils.catching;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zc.async.nio.concurrent.eventloop.EventLoop.Event;


/**
 * @author coderzc
 * Created on 2020-06-28
 */
public class BlockEvent {
    private static final Logger logger = LoggerFactory.getLogger(BlockEvent.class);
    private static final Set<Event> eventRegisterSet = new HashSet<>();

    public static void main(String[] args) {
        // 向事件监听器添加事件
        eventRegisterSet.add(new Event<Integer>(KEY_EVENT, eventCode -> {
            logger.info("eventCode：" + eventCode);
            if (KeyEvent.VK_ENTER == eventCode) {
                logger.info("你点击了回车");
            } else if (KeyEvent.VK_SPACE == eventCode) {
                logger.info("你点击了空格");
                catching(() -> Thread.sleep(1000), Throwable::printStackTrace);
            }
        }));

        while (true) {
            catching(() -> {
                // 获取键盘输入，等待用户按下，阻塞中。。。
                int keyCode = System.in.read();

                // 遍历事件注册集合，找到注册了该按键的事件执行
                for (Event event : eventRegisterSet) {
                    if (event.eventType == KEY_EVENT) {
                        event.consumer.accept(keyCode);
                        break;
                    }
                }
            }, Throwable::printStackTrace);
        }
    }
}
