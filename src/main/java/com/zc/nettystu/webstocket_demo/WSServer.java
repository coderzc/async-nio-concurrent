package com.zc.nettystu.webstocket_demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by zhaocong on 2019-06-27
 */
public class WSServer {

    public static void main(String[] args) {

        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup subGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap()
                    .group(mainGroup,subGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WSServerHandlerInitialzer());

            ChannelFuture channelFuture = server.bind(8088).sync();

            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mainGroup.shutdownGracefully();
            subGroup.shutdownGracefully();
        }

    }
}
