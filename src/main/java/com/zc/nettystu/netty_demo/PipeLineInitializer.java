package com.zc.nettystu.netty_demo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Created by zhaocong on 2019-06-12
 *
 * 初始化Pipeline，channel注册后，会执行里面对应的初始化方法 ---> 这东西类似拦截器链
 */
public class PipeLineInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // 通过SocketChannel获取对应的管道
        ChannelPipeline pipeline = channel.pipeline();

        // pipeline 添加handler
        // 添加http编解码器到pipeline
        pipeline.addLast("HttpServerCodec",new HttpServerCodec());

        // 添加自定义的handel
        pipeline.addLast("CustomHandel",new CustomHandler());
    }



}
