package com.zc.async.nio.concurrent.netty_demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Created by coderzc on 2019-06-12
 */
@Service
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Value("${netty.port}")
    private Integer nettyServerPort;

    /**
     * 定义一对线程组
     */
    // 主线程组，用于接受来自客户端的连接，但不做任何处理，和老板一样 ---> Acceptor
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    // 从线程组，处理来自主线程组的任务 ----> reactor
    EventLoopGroup workGroup = new NioEventLoopGroup(10);
    /**
     * 1. 创建一个线程执行器
     * new ThreadPerTaskExecutor(newDefaultThreadFactory());
     *
     * 2. 创建 NioEventLoop
     * io.netty.channel.nio.NioEventLoop#NioEventLoop
     *
     * 3. 创建一个selector
     * io.netty.channel.nio.NioEventLoop#openSelector()
     */

    /**
     * 创建一个netty启动器,设置启动参数
     */
    ServerBootstrap serverBootstrap = new ServerBootstrap()
            .group(bossGroup, workGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 128) //配置ServerSocketChannel 参数 SO_BACKLOG--->accept就绪队列大小
            .childOption(ChannelOption.TCP_NODELAY, true) //配置SocketChannel 参数 TCP_NODELAY--->true 关闭nagle算法
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .handler(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                    logger.info("channelRegistered");
                }

                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    logger.info("channelActive");
                }

                @Override
                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                    logger.info("handlerAdded");
                }
            })
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    // 通过SocketChannel获取对应的管道
                    ChannelPipeline pipeline = socketChannel.pipeline();

                    // pipeline 添加handler
                    // 添加http编解码器到pipeline
                    pipeline.addLast("HttpServerCodec", new HttpServerCodec());

                    // 添加自定义的handel
                    pipeline.addLast("CustomHandler", new CustomHandler());
                }
            });

    /**
     * 启动服务
     *
     * @throws InterruptedException
     */
    public void startServer() throws InterruptedException {
        // 绑定端口并启动监听 sync 表示以同步方式启动; socket()、bind()、listen() ChannelFuture相当于开启一个线程
        ChannelFuture channelFuture = serverBootstrap.bind(nettyServerPort).sync();
        logger.info("NettyServer start success listen port {}", nettyServerPort);
        // 阻塞。。。。

        // 用于关闭channel
        channelFuture.channel().closeFuture().addListener(future -> {
            // 关闭线程组
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        });
    }

}
