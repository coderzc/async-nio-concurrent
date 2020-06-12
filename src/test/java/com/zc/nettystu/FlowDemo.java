package com.zc.nettystu;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;

/**
 * @author coderzc
 * Created on 2020-06-11
 */
public class FlowDemo {
    public static void main(String[] args) {

        // 定义订阅者
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            private Subscription subscription;
            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("接受到数据：" + item);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                this.subscription.cancel();
            }

            @Override
            public void onComplete() {
                System.out.println("publisher over");
            }
        };

        // 定义发布者
        try(SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>()) {
            // 建立关系
            publisher.subscribe(subscriber);

            // 生产数据
            for (int i = 0; i < 1000; i++) {
                publisher.submit(i);
            }
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
