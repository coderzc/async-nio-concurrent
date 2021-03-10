package com.zc.async.nio.concurrent.utils;

import java.util.concurrent.ThreadFactory;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

public class NettyUtils {

    /** Creates a Netty EventLoopGroup based on the IOMode. */
    public static EventLoopGroup createEventLoop(IOMode mode, int numThreads,
                                                 String threadPrefix) {
        ThreadFactory threadFactory = createThreadFactory(threadPrefix);

        switch (mode) {
            case NIO:
                return new NioEventLoopGroup(numThreads, threadFactory);
            case EPOLL:
                return new EpollEventLoopGroup(numThreads, threadFactory);
            default:
                throw new IllegalArgumentException("Unknown io mode: " + mode);
        }
    }

    /** Returns the correct (client) SocketChannel class based on IOMode. */
    public static Class<? extends Channel> getClientChannelClass(IOMode mode) {
        switch (mode) {
            case NIO:
                return NioSocketChannel.class;
            case EPOLL:
                return EpollSocketChannel.class;
            default:
                throw new IllegalArgumentException("Unknown io mode: " + mode);
        }
    }

    /** Returns the correct ServerSocketChannel class based on IOMode. */
    public static Class<? extends ServerChannel> getServerChannelClass(IOMode mode) {
        switch (mode) {
            case NIO:
                return NioServerSocketChannel.class;
            case EPOLL:
                return EpollServerSocketChannel.class;
            default:
                throw new IllegalArgumentException("Unknown io mode: " + mode);
        }
    }

    /** Creates a new ThreadFactory which prefixes each thread with the given name. */
    public static ThreadFactory createThreadFactory(String threadPoolPrefix) {
        return new DefaultThreadFactory(threadPoolPrefix, true);
    }

}
