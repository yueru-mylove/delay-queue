package com.miracle.queue.youzan.loadbalancer.roundrobin;

public class Node implements Comparable<Node> {

    final Invoker invoker;
    final Integer weight;
    Integer effectiveWeight;
    Integer currentWeight;

    public Node(Invoker invoker, Integer weight) {
        this.invoker = invoker;
        this.weight = weight;
        this.effectiveWeight = weight;
        this.currentWeight = 0;
    }

    @Override
    public int compareTo(Node o) {
        return currentWeight > o.currentWeight ? 1 :
                (currentWeight.equals(o.currentWeight) ? 0 : -1);
    }

    public void onInvokerSuccess() {
        if (effectiveWeight < this.weight) {
            effectiveWeight++;
        }
    }

    public void onInvokeFail() {
        effectiveWeight--;
    }
}
