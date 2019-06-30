package com.zc.nettystu.webstocket_demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by zhaocong on 2019-06-27
 */
@Component
public class WSServer {

    private EventLoopGroup mainGroup;
    private EventLoopGroup subGroup;
    private ServerBootstrap server;
    private ChannelFuture channelFuture;

    @Value("${netty.websocket.port}")
    private Integer WSServerPort;

    private static final Logger logger = LoggerFactory.getLogger(WSServer.class);

    /*
    单例模式  TODO spring 默认就是单例子
     */
//    private WSServer() {
//        this.init();
//    }
//
//    private static class WSServerSingleton {
//        static final WSServer instance = new WSServer();
//    }
//
//    public static WSServer getInstance() {
//        return WSServerSingleton.instance;
//    }


    public WSServer() {
        init();
    }

    private void init() {
        mainGroup = new NioEventLoopGroup();
        subGroup = new NioEventLoopGroup();

        server = new ServerBootstrap()
                .group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WSServerHandlerInitialzer());
    }


    public void startServer() {
        channelFuture = server.bind(WSServerPort);
        logger.info("WSServer start success listen port {}", WSServerPort);
    }

/*
spring 帮我们管理bean
 */
//    public void closeServer() {
//        this.channelFuture.channel().closeFuture();
//        this.mainGroup.shutdownGracefully();
//        this.subGroup.shutdownGracefully();
//    }
}
