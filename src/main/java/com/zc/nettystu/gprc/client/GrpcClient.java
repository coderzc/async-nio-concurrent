package com.zc.nettystu.gprc.client;

import com.alibaba.fastjson.JSON;
import com.zc.protojava.routeguide.Feature;
import com.zc.protojava.routeguide.Point;
import com.zc.protojava.routeguide.RouteGuideGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GrpcClient {
    private static final Logger logger = LoggerFactory.getLogger(GrpcClient.class);

    public void helloGrpcClient() {
        Point point = Point.newBuilder().setLatitude(1).setLongitude(2).build();
        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", 8080)
                .usePlaintext()
                .build();
        RouteGuideGrpc.RouteGuideBlockingStub routeGuideBlockingStub = RouteGuideGrpc.newBlockingStub(channel);
        Feature res = routeGuideBlockingStub.getFeature(point);
        logger.info("RouteGuideGrpc getFeature res:{}", JSON.toJSON(res));
    }

}
