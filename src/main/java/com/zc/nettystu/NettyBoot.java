package com.zc.nettystu;

import com.zc.nettystu.webstocket_demo.WSServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyBoot implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(NettyBoot.class);

    @Autowired
    private WSServer wsServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            try {
                wsServer.startServer();
            } catch (Exception e) {
                logger.error("NettyBoot error:", e);
            }
        }
    }


}
