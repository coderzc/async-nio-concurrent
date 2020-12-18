package com.zc.async.nio.concurrent.vertx;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VertxServer {
    private static final Logger logger = LoggerFactory.getLogger(VertxServer.class);

    @Value("${vertx.port}")
    private Integer vertxServerPort;

    public static Vertx vertx;

    static {
        vertx = Vertx.vertx();
    }

    public void startServer() {
        vertx.createHttpServer().requestHandler(req -> {
            String uri = req.uri();
            logger.info(uri);
            HttpServerResponse response = req.response();
            response.putHeader("Content-type", "text/html;charset=utf-8");
            response.end("hello word");
        }).listen(vertxServerPort);
        logger.info("VertxServer start success listen port {}", vertxServerPort);
    }

    public void timer(){
        vertx.setTimer(1000, timerId -> {
            System.out.println("当前定时器id是: " + timerId);
        });
    }
}
