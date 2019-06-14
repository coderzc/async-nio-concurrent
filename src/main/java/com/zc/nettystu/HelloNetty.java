package com.zc.nettystu;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by zhaocong on 2019-06-12
 */
public class HelloNetty {

    public static void main(String[] args) throws InterruptedException {

        /**
         定义一对线程组
         */

        // 主线程组，用于接受来自客户端的连接，但不做任何处理，和老板一样
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 从线程组，处理来自主线程组的任务
        EventLoopGroup workGroup = new NioEventLoopGroup();

        /**
         * 创建一个netty启动器,设置启动参数
         */
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(null);
        try {

            // 绑定端口并启动监听 sync 表示以同步方式启动;
            ChannelFuture channelFuture = serverBootstrap.bind(8088).sync();

            // 用于监听关闭channel
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            throw e;
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

        }

    }
}
