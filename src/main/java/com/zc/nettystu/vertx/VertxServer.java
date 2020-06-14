package com.zc.nettystu.vertx;

import com.zc.nettystu.NettyStuApplication;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VertxServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyStuApplication.class);
    public static Vertx vertx = null;

    static {
        vertx = Vertx.vertx();
    }

    public void startServer() {
        vertx.createHttpServer().requestHandler(req -> {
            String uri = req.absoluteURI();
            logger.info(uri);
        }).listen(9000);
    }
}
