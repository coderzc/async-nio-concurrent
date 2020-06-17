package com.zc.async.nio.concurrent.netty_webstocket_demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class CustomHttpHandler extends SimpleChannelInboundHandler<HttpRequest> {

    public static AttributeKey<String> clientIPKey = AttributeKey.valueOf("clientIP");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest mReq) throws Exception {
        Attribute<String> channelAttr = ctx.channel().attr(clientIPKey);

        String clientIP = mReq.headers().get("X-Forwarded-For");
        if (clientIP == null) {
            InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
                    .remoteAddress();
            clientIP = insocket.getAddress().getHostAddress();
        }

        // 把客户端ip放入channel上下文
        if (channelAttr.get() == null) {
            channelAttr.set(clientIP);
        } else {
            System.out.println("channel key :" + channelAttr.get());
        }

        // 继续下一个handler
        ctx.fireChannelRead(mReq);
    }
}