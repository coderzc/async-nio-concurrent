package com.zc.nettystu;

import com.zc.nettystu.webstocket_demo.WSServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class NettyRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(NettyRunner.class);

    @Autowired
    private WSServer wsServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            wsServer.startServer();
        } catch (Exception e) {
            logger.error("NettyBoot error:", e);
        }
    }
}
