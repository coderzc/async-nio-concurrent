package com.zc.nettystu;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by zhaocong on 2019-06-12
 *
 * 初始化器，channel注册后，会执行里面对应的初始化方法
 */
public class HelloServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // 通过SocketChannel获取对应的管道
        ChannelPipeline pipeline = channel.pipeline();

        // pipeline(相当于拦截器 Handler 组)
//        pipeline.addAfter()
    }



}
