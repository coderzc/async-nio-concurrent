package com.zc.nettystu.webstocket_demo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by zhaocong on 2019-06-27
 */
public class WSServerHandlerInitialzer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 获取pipeline
        ChannelPipeline pipeline = ch.pipeline();

        // 添加编解码处理器 (WS 基于 http)
        pipeline.addLast(new HttpServerCodec());

        // 添加大数据流支持
        pipeline.addLast(new ChunkedWriteHandler());

        // 对httpMessage进行聚合--> FullHttpRequest、FullHttpResponse
        pipeline.addLast(new HttpObjectAggregator(1024*64));

        // ----------------------- 以上是对HTTP的处理 -------------------

        /**
         * WS 协议处理器
         * handshaking(close、ping、pong)
         * 将ws数据分装成frame
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));

        // 添加自定义handler
        pipeline.addLast(new ChatHandler());

    }
}
