package com.zc.nettystu.utils;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author coderzc
 * Created on 2020-06-12
 */
public class MapReduceForkJoin {
    private static ForkJoinPool pool = null;

    static {
        pool = new ForkJoinPool();
    }

    protected static class MapReduceTask<T, V> extends RecursiveTask<V> {
        int maxCountRange = 0;// 最大计算范围
        List<T> data; // 数据源
        int startNum, endNum; // 计算范围
        BiFunction<T, V, V> mapFunction = null;
        BiFunction<V, V, V> reduceFunction = null;
        Supplier<V> initResFunc = null;


        public MapReduceTask(List<T> data, int maxCountRange, Supplier<V> initResFunc, BiFunction<T, V, V> mapFunction,
                             BiFunction<V, V, V> reduceFunction) {
            this.maxCountRange = maxCountRange;
            this.startNum = 0;
            this.data = Collections.unmodifiableList(data);
            this.endNum = (data.size() - 1);
            this.mapFunction = mapFunction;
            this.initResFunc = initResFunc;
            this.reduceFunction = reduceFunction;
        }

        public MapReduceTask(List<T> data, int maxCountRange, Supplier<V> initResFunc, BiFunction<T, V, V> mapFunction,
                             BiFunction<V, V, V> reduceFunction, int startNum,
                             int endNum) {
            this.data = Collections.unmodifiableList(data);
            this.maxCountRange = maxCountRange;
            this.startNum = startNum;
            this.endNum = endNum;
            this.mapFunction = mapFunction;
            this.initResFunc = initResFunc;
            this.reduceFunction = reduceFunction;
        }


        @Override
        protected V compute() {
            V res = initResFunc.get();
            if (endNum - startNum >= maxCountRange) {//如果这次计算的范围大于了计算时规定的最大范围，则进行拆分
                int middle = (startNum + endNum) / 2;
                MapReduceTask<T, V> subMapReduceTask1 =
                        new MapReduceTask<>(this.data, this.maxCountRange, this.initResFunc, this.mapFunction,
                                this.reduceFunction,
                                startNum, middle);
                MapReduceTask<T, V> subMapReduceTask2 =
                        new MapReduceTask<>(this.data, this.maxCountRange, this.initResFunc, this.mapFunction,
                                this.reduceFunction,
                                middle + 1, endNum);
                //拆分后，执行fork
                invokeAll(subMapReduceTask1, subMapReduceTask2);

                // merge 结果
                res = reduceFunction.apply(subMapReduceTask1.join(), res);
                res = reduceFunction.apply(subMapReduceTask2.join(), res);
            } else {//在范围内，则进行计算
                for (; startNum <= endNum; startNum++) {
                    res = mapFunction.apply(data.get(startNum), res);
                }
            }
            return res;
        }
    }

    /**
     * MapReduce 并行执行器
     *
     * @param data           数据源
     * @param mapCount       划分为多少个map
     * @param initResFunc    结果初始化函数
     * @param mapFunction    map函数 T,V ---> V
     * @param reduceFunction reduce函数 V,V ---> V
     * @param <T>            输入数据类型
     * @param <V>            返回结果类型
     */
    public static <T, V> V exec(List<T> data, int mapCount, Supplier<V> initResFunc,
                                BiFunction<T, V, V> mapFunction,
                                BiFunction<V, V, V> reduceFunction) {
        if (data == null) {
            return null;
        }
        int maxCountRange = (int) Math.ceil((double) data.size() / mapCount);
        MapReduceTask<T, V> mapReduceTask =
                new MapReduceTask<>(data, maxCountRange, initResFunc, mapFunction, reduceFunction);
        return pool.invoke(mapReduceTask);
    }


    public static void main(String[] args) throws IOException {
//          testWordCountMapReduce();
        testFactorial(100000);
    }

    // 词频统计
    public static void testWordCountMapReduce() {
        List<String> data = null;
        try {
            byte[] bytes = Files.readAllBytes(Paths.get("./1.txt"));
            String[] split = new String(bytes, StandardCharsets.UTF_8).split("");
            data = Lists.newArrayList(split);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        long startTime = System.currentTimeMillis();
        Map<String, Long> result = MapReduceForkJoin.exec(data, 10, HashMap::new
                , (word, res) -> {// map阶段 T,V ---> V  (T是数据,V是该阶段历史计算结果)
                    if (res.putIfAbsent(word, 1L) != null) {
                        res.computeIfPresent(word, (k, v) -> v + 1);
                    }
                    return res;
                }, (join, res) -> {// reduce阶段 V,V ---> V
                    join.forEach((word, v) -> {
                        if (res.putIfAbsent(word, 1L) != null) {
                            res.computeIfPresent(word, (k, v_) -> v + v_);
                        }
                    });
                    return res;
                });
        long endTime = System.currentTimeMillis();
        System.out.println(result);
        System.out.println("MapReduceForkJoin: " + (endTime - startTime));
    }

    // 计算n的阶乘
    public static void testFactorial(int n) {
        // 生成 1,2,3,4,5...n
        List<Integer> list = Stream.iterate(1, item -> item + 1).limit(n).collect(Collectors.toList());

        int mapCount = 10000;
        System.out.println("mapCount: " + mapCount + "\n");

        long startTime3 = System.currentTimeMillis();
        BigDecimal result3 = list.stream().map(x -> new BigDecimal(String.valueOf(x))).reduce(
                BigDecimal::multiply).orElse(null);
        System.out.println(result3);
        long endTime3 = System.currentTimeMillis();

        System.out.println("stream: " + (endTime3 - startTime3) + "\n");

        long startTime2 = System.currentTimeMillis();
        BigDecimal result2 = list.stream().parallel().map(x -> new BigDecimal(String.valueOf(x))).reduce(
                BigDecimal::multiply).orElse(null);
        System.out.println(result2);
        long endTime2 = System.currentTimeMillis();

        System.out.println("parallel stream: " + (endTime2 - startTime2) + "\n");


        long startTime = System.currentTimeMillis();
        BigDecimal result = MapReduceForkJoin.exec(list, mapCount, () -> new BigDecimal("1"),
                (x1, x2) -> new BigDecimal(String.valueOf(x1)).multiply(new BigDecimal(String.valueOf(x2))),
                BigDecimal::multiply);
        System.out.println(result);
        long endTime = System.currentTimeMillis();

        System.out.println("MapReduceForkJoin: " + (endTime - startTime));

    }


}
