package com.zc.async.nio.concurrent.reactor;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Lists;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class ReactorDemo {
    public static void main(String[] args) throws IOException {
        // Mono 0-1 个元素
        // Flux 1-n 个元素
        // reactor = Stream + Flow
        ArrayList<String> words = Lists.newArrayList("Hello", "World", "!");
        Mono<String> helloWorld = Mono.just("Hello World");
        Flux<String> fewWords = Flux.just("Hello", "World");
        Flux<String> manyWords = Flux.fromIterable(words);

        //        manyWords.map(Integer::parseInt)
        //                .subscribe(System.out::println);

        helloWorld.flatMap(x -> Mono.fromSupplier(() -> String.format("%s len is :%s", x, x.length())))
                .map(String::trim)
                ;

        helloWorld.delaySubscription(Duration.ofSeconds(5)).subscribe(x->{
            System.out.println(x);
        });

        System.in.read();
    }
}
