package com.zc.async.nio.concurrent.netty;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author coderzc
 * Created on 2020-07-09
 */
public class PortScanNio {
    private static final Logger logger = LoggerFactory.getLogger(PortScanNio.class);
    private final NioEventLoopGroup nioEventLoopGroup;
    private final Bootstrap clientBootstrap = new Bootstrap();

    public PortScanNio() {
        this(0);
    }

    public PortScanNio(int nThread) {
        nioEventLoopGroup = new NioEventLoopGroup(nThread);
        clientBootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                //                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                    }
                });
    }

    public Set<Integer> scanPort(String scanIp, int minPort, int maxPort) throws InterruptedException {
        Set<Integer> openPorts = Sets.newConcurrentHashSet();
        CountDownLatch countDownLatch = new CountDownLatch(maxPort - minPort + 1);
        for (int i = minPort; i <= maxPort; i++) {
            submitConnect(scanIp, i, openPorts, countDownLatch);
        }
        countDownLatch.await(20, TimeUnit.MINUTES);
        return openPorts;
    }

    public Set<Integer> scanPort(String scanIp, Set<Integer> portSet) throws InterruptedException {
        Set<Integer> openPorts = Sets.newConcurrentHashSet();
        CountDownLatch countDownLatch = new CountDownLatch(portSet.size());
        for (Integer port : portSet) {
            submitConnect(scanIp, port, openPorts, countDownLatch);
        }
        countDownLatch.await(20, TimeUnit.MINUTES);
        return openPorts;
    }

    public void submitConnect(String ip, Integer port, Set<Integer> openPorts,
            CountDownLatch countDownLatch) {
        ChannelFuture channelFuture = clientBootstrap.connect(ip, port);
        channelFuture.channel().config().setConnectTimeoutMillis(1500);
        if (channelFuture.isDone() && channelFuture.isSuccess()) {
            openPorts.add(port);
            channelFuture.channel().close();
            countDownLatch.countDown();
            return;
        }
        channelFuture.addListener(future -> {
            try {
                if (future.isSuccess()) {
                    openPorts.add(port);
                    logger.info("{}:{} is open", ip, port);
                }
            } catch (Exception e){
                // 如果发生错误，则访问描述原因的Throwable
                //                Throwable cause = future.cause();
                //                cause.printStackTrace();
            }finally {
                channelFuture.channel().close();
                countDownLatch.countDown();
            }
        });
    }

    public void close() {
        this.nioEventLoopGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        try {
            PortScanNio portScanNio = new PortScanNio();
            Set<Integer> openPorts = portScanNio.scanPort("127.0.0.1", 1, 65535);
            logger.info("openPorts:{}", openPorts);
            portScanNio.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
