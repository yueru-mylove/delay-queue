package com.miracle.queue.youzan.loadbalancer.roundrobin;

public class DefaultInvoker implements Invoker {

    private String id;

    public DefaultInvoker(String id) {
        this.id = id;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String id() {
        return this.id;
    }
}
