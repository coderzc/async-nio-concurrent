package com.zc.nettystu.gprc.service;

import com.zc.proto.routeguide.*;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class RouteGuideServiceImpl extends RouteGuideGrpc.RouteGuideImplBase {
    @Override
    public void getFeature(Point request, StreamObserver<Feature> responseObserver) {
        super.getFeature(request, responseObserver);
    }

    @Override
    public void listFeatures(Rectangle request, StreamObserver<Feature> responseObserver) {
        super.listFeatures(request, responseObserver);
    }

    @Override
    public StreamObserver<Point> recordRoute(StreamObserver<RouteSummary> responseObserver) {
        return super.recordRoute(responseObserver);
    }

    @Override
    public StreamObserver<RouteNote> routeChat(StreamObserver<RouteNote> responseObserver) {
        return super.routeChat(responseObserver);
    }
}
