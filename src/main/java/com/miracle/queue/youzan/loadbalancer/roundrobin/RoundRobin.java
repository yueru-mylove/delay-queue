package com.miracle.queue.youzan.loadbalancer.roundrobin;

import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobin {

    private static final AtomicInteger next = new AtomicInteger(0);

    public static String select(String[] buckets) {
        Assert.notEmpty(buckets, "轮训列表不能为空！");
        return buckets[next.getAndIncrement() % buckets.length];
    }


}
