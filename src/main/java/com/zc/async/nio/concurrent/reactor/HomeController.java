package com.zc.async.nio.concurrent.reactor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private DataSource dataSource;

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

    @GetMapping("/database")
    public List<Map<String, Object>> getData() {
        String sql = "select * from students";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        return maps;
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
