package com.zc.async.nio.concurrent.eventloop;

import static com.zc.async.nio.concurrent.eventloop.EventDrive.Event.KEY_EVENT;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.zc.async.nio.concurrent.eventloop.EventDrive.Event;

/**
 * @author coderzc
 * Created on 2020-06-28
 */
public class BlockEvent {
    private static final Set<Event> eventRegisterSet = new HashSet<>();
    public static void main(String[] args) {
        // 向事件监听器添加事件
        eventRegisterSet.add(new EventDrive.Event(KEY_EVENT,KeyEvent.VK_ENTER, () -> {
            System.out.println("你点击了回车");
        }));

        while (true){
            try {
                // 获取键盘输入，等待用户按下，阻塞中。。。
                int read = System.in.read();

                // 遍历事件注册集合，找到注册了该按键的事件执行
                for (Event event : eventRegisterSet) {
                    if(event.keyEventCode==read){
                        event.callable.run();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
