package com.zc.nettystu.webstocket_demo;

import com.zc.nettystu.netty_demo.CustomHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Created by zhaocong on 2019-06-27
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        // 获取客户端传过来的消息
        String text = msg.text();
        logger.info("\033[31;m"+"接收到数据:{}，from channel id:{}\033[0m", text, ctx.channel().id().asLongText());


        // 向所有channel广播消息
        String sendMsgContent = "服务器接受到消息：" + LocalDateTime.now() + "----->" + text;

//        for (Channel client : clients) {
//            client.writeAndFlush(new TextWebSocketFrame(sendMsgContent));
//        }

        clients.writeAndFlush(new TextWebSocketFrame(sendMsgContent)); //ChannelGroup的广播方法

    }

    //客户端上线
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // channel打开后放入ChannelGroup
        clients.add(ctx.channel());
    }

    //客户端下线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // clients.remove(ctx.channel());  //channel 关闭时，netty会自动把他从ChannelGroup移除
        logger.info("channel remove id:{}", ctx.channel().id().asLongText());
    }
}
