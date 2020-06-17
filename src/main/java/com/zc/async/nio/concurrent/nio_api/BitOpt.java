package com.zc.async.nio.concurrent.nio_api;

import java.nio.channels.SelectionKey;

/**
 * 位运算
 * @author: coderzc
 */
public class BitOpt {
    public static void main(String[] args) {
        /*
         * SelectionKey.OP_READ = 1 << 0 --->1
         *
         * SelectionKey.OP_WRITE = 1 << 2 --->4
         *
         * SelectionKey.OP_CONNECT = 1 << 3 --->8
         *
         * SelectionKey.OP_ACCEPT = 1 << 4 --->16
         */

        int opt = (SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        System.out.println(opt); // 5

        opt = (opt | SelectionKey.OP_WRITE);

        System.out.println(opt); // 5

        boolean hasWrite = (opt & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE;
        System.out.println(hasWrite);// true 代表里面有SelectionKey.OP_WRITE(1 << 2)

        boolean hasAccept = (opt & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT;
        System.out.println(hasAccept);// false 代表里面没有有SelectionKey.OP_ACCEPT(1 << 4)


        opt = (opt & ~SelectionKey.OP_WRITE);
        System.out.println(opt); // 1 代表移除SelectionKey.OP_WRITE(1 << 2)


        opt = SelectionKey.OP_READ | SelectionKey.OP_CONNECT;
        System.out.println(opt & SelectionKey.OP_WRITE);

    }
}
