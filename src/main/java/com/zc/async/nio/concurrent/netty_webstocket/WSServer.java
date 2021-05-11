package com.zc.async.nio.concurrent.netty_webstocket;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by coderzc on 2019-06-27
 */
@Service
public class WSServer {

    private EventLoopGroup mainGroup;
    private EventLoopGroup subGroup;
    private ServerBootstrap server;
    private ChannelFuture channelFuture;

    @Value("${netty.websocket.port}")
    private Integer WSServerPort;

    private static final Logger logger = LoggerFactory.getLogger(WSServer.class);

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
        channelFuture = server.bind(
                new InetSocketAddress("0.0.0.0", WSServerPort));
        channelFuture.channel().closeFuture().addListener(future -> {
            mainGroup.shutdownGracefully();
            subGroup.shutdownGracefully();
        });

        channelFuture.syncUninterruptibly();
        logger.info("WSServer start success listen port {}", WSServerPort);
    }
}
