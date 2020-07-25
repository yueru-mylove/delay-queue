package com.miracle.queue.youzan.loadbalancer.roundrobin;

public interface Invoker {

    boolean isAvailable();

    String id();
}
