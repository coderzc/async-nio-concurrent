package com.zc.async.nio.concurrent.nio_api;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBufTest {
    public static void main(String[] args) {
        byte[] bytes = new byte[]{1,2,3,4,5};
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        System.out.println(String.format("byteBuf class is:%s",
                                        byteBuf.getClass().getName()));
    }
}
