package com.zc.async.nio.concurrent;

import com.zc.async.nio.concurrent.netty_demo.NettyServer;
import com.zc.async.nio.concurrent.netty_webstocket_demo.WSServer;
import com.zc.async.nio.concurrent.vertx.VertxServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyStuApplication implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(NettyStuApplication.class);

    @Autowired
    private WSServer wsServer;

    @Autowired
    private NettyServer nettyServer;

    @Autowired
    private VertxServer vertxServer;

    public static void main(String[] args) {
        SpringApplication.run(NettyStuApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            wsServer.startServer();
            nettyServer.startServer();
            vertxServer.startServer();
        } catch (Exception e) {
            logger.error("NettyBoot error:", e);
        }
    }

}
