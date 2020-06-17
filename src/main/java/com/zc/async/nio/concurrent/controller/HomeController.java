package com.zc.async.nio.concurrent.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/1")
    public String get1() {
        logger.info("start get1");
        String str = createStr();
        logger.info("end get1");
        return str;
    }

    @GetMapping("/2")
    public Mono<String> get2() {
        logger.info("start get2");
        Mono<String> stringMono = Mono.fromSupplier(this::createStr);
        logger.info("end get2");
        return stringMono;
    }

    private String createStr() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return UUID.randomUUID().toString();
    }


}
