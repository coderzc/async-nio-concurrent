package com.zc.async.nio.concurrent.lamdba;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * @author coderzc
 * Created on 2020-06-09
 */
public class LambdaDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int[] nums = {33, 65, 98, 78, 56};
        int asInt = IntStream.of(nums).min().getAsInt();
        System.out.println(asInt);

        int asInt1 = IntStream.of(nums).parallel().min().getAsInt();
        System.out.println(asInt1);

        printFun(fun1(), 10);

        f1().run();

        String str = "my name is 007";
        Stream.of(str.split(" ")).flatMap(x -> x.chars().boxed())
                .map(x -> (char) x.intValue()).peek(System.out::println).collect(Collectors.toList());

        Character[] chars = {'a', 'b', 'c'};
        Stream.of(chars).forEach(System.out::println);

        //        Arrays.stream(str.split(" ")).map(String::chars).map(IntStream::boxed).map(x ->{
        //            return x.<Character> map(y -> (char) y.intValue());
        //        }).flatMap(Stream::of)
        //                .peek(System.out::println).collect(Collectors.toList());

        str.chars().mapToObj(x -> (char) x).forEach(System.out::println);


        List<String> collect = Stream.of(str.split(" ")).collect(Collectors.toList());
        System.out.println(collect);

        String letter = Stream.of(str.split(" ")).map(String::trim).reduce((s1, s2) -> s1 + "|" + s2).orElse("");
        System.out.println(letter);

        // 带初始指的reduce
        Integer totalLen = Stream.of(str.split(" ")).map(String::length).reduce(0, (s1, s2) -> s1 + s2);
        System.out.println(totalLen);


        ArrayList<Integer> integers = Lists.newArrayList(1, 2, 78, 4, 18, 6, 7, 9, 13, 56);
        List<List<Integer>> partition = Lists.partition(integers, 3);

        Integer maxMapReduce =
                partition.stream().parallel()
                        .map(x -> {
                            System.out.println(Thread.currentThread().getName());
                            return x.stream().max(Integer::compare).orElse(0);
                        })
                        .reduce(0, (x1, x2) -> {
                            System.out.println("reduce: " + Thread.currentThread().getName());
                            return Math.max(x1, x2);
                        });
        System.out.println(maxMapReduce);


        ForkJoinPool pool = new ForkJoinPool(5);
        ForkJoinTask<Integer> submit = pool.submit(() -> partition.stream().parallel()
                .map(x -> x.stream().max(Integer::compare).orElse(0))
                .reduce(0, Math::max));
        System.out.println(submit.get());
        pool.shutdown();

        HashMap<String, String> map = new HashMap<>();
        map.put("a", "123");
        map.put("b", "456");

        String c = map.putIfAbsent("c", String.valueOf(System.currentTimeMillis()));
        System.out.println(c);
        System.out.println(map);

        // 如果不存在则放入指定值
        String d = map.putIfAbsent("d", null);
        System.out.println(d);
        System.out.println(map);

        // null 不会被添加, 并且返回新值
        String e = map.computeIfAbsent("e", k -> "***");
        System.out.println(e);
        System.out.println(map);

        // 如果k,v 都存在才执行，并返回新值
        String d2 = map.computeIfPresent("d", (k, v) -> "--");
        System.out.println(d2);
        System.out.println(map);

        // 如果 v 不存在直接put新的值，如果存在则put 表达式的值，并返回新的值 (返回 null k会被删除)
        String d3 = map.merge("d", "1", String::concat);
        System.out.println(d3);
        System.out.println(map);

        // mapFunction 返回 null k会被删除
        String c2 = map.computeIfPresent("c", (k, v) -> null);
        System.out.println(c2);
        System.out.println(map);

    }


    public static Function<Integer, String> fun1() {
        return x -> new DecimalFormat("#,###").format(x);
    }

    public static <T> void printFun(Function<T, String> function, T param) {
        System.out.println(function.apply(param));
    }

    public static Runnable f1() {
        int i = 1;
        return () -> System.out.println(i);
    }

    public static <V> Future<V> then(Function<String, V> function) {
        V apply = function.apply(null);
        CompletableFuture<V> future = new CompletableFuture<>();
        future.complete(apply);
        return future;
    }

    public static Future<Void> then(Consumer<String> consumer) {
        consumer.accept(null);
        return new CompletableFuture<>();
    }

    public static Future<Void> then(Runnable runnable) {
        runnable.run();
        return new CompletableFuture<>();
    }

    /**
     * 实现类似 python 计数器的功能，怎么写最简洁
     * python 代码：
     * from collections import Counter
     * colors = ['red', 'blue', 'red', 'green', 'blue', 'blue']
     * c = Counter(colors)
     * print (dict(c))
     * {'red': 2, 'blue': 3, 'green': 1}
     */
    @Test
    public void counter() {
        String[] arr = {"red", "blue", "red", "green", "blue", "blue"};

        // stream 版
        Map<Object, Long> counter =
                Arrays.stream(arr).collect(Collectors.groupingBy(String::toString, Collectors
                        .counting()));
        System.out.println(counter);

        // map merge 版 (最简洁，写算法题推荐)
        Map<Object, Long> cnt = new HashMap<>();
        Arrays.stream(arr).forEach(item -> cnt.merge(item, 1L, Long::sum));
        System.out.println(cnt);

        // map compute 版
        Map<Object, Long> cnt2 = new HashMap<>();
        Arrays.stream(arr).forEach(item -> cnt2.compute(item, (k, v) -> v == null ? 1 : v + 1));
        System.out.println(cnt2);

        // 线程安全版
        Map<Object, AtomicLong> concnt = new ConcurrentHashMap<>();
        Arrays.stream(arr).forEach(item -> concnt.computeIfAbsent(item, k -> new AtomicLong(0)).incrementAndGet());

        System.out.println(concnt);
    }

}
