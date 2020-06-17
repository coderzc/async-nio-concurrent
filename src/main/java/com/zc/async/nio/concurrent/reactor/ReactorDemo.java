package com.zc.async.nio.concurrent.reactor;

import org.assertj.core.util.Lists;
import reactor.core.publisher.Flux;


public class ReactorDemo {
    public static void main(String[] args) {
        // Mono 0-1 个元素
        // Flux 1-n 个元素
        // reactor = Stream + Flow

        Flux.fromIterable(Lists.newArrayList("1", "2", "3"))
                .map(Integer::parseInt)
                .subscribe(System.out::println);
    }
}
