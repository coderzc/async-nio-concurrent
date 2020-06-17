package com.zc.async.nio.concurrent.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义助手类
 * Created by coderzc on 2019-06-26
 */
public class CustomHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger logger = LoggerFactory.getLogger(CustomHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 获取channel
        Channel channel = ctx.channel();

        // http 请求
        if (msg instanceof HttpRequest) {

            System.out.println(msg.getClass().getName());

            // 显示客户端远程地址
            logger.info("remoteAddress:" + channel.remoteAddress().toString());

            // 定义发送数据消息并写入ByteBuf
            ByteBuf content = Unpooled.copiedBuffer("hi io.netty~，你好 😂", CharsetUtil.UTF_8);

            // 构建一个http response
            FullHttpResponse response = getFullHttpResponse(content);

            // 向客户端channel写入消息
            ctx.writeAndFlush(response);
        }


        // http 响应
        if (msg instanceof HttpResponse) {
        }

    }

    // 构建一个http response
    private FullHttpResponse getFullHttpResponse(ByteBuf content) {
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK, content);

        //设置响应头
        response.headers()
                .add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN + ";charset=UTF-8")
                .add(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        return response;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelRegistered 注册");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelUnregistered 移除");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelActive 活跃");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelInactive 不活跃");
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelReadComplete 读取完毕");
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.debug("userEventTriggered 用户事件触发");
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelWritabilityChanged 可写状态改变");
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug("exceptionCaugh 捕获到异常");
        cause.printStackTrace();
        ctx.close();// 关闭连接
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.debug("handlerAdded");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        logger.debug("handlerRemoved");
        super.handlerRemoved(ctx);
    }
}
