package com.miracle.queue.youzan.loadbalancer.roundrobin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeightedRoundRobin {

    private final List<Node> nodes;

    public WeightedRoundRobin(Map<Invoker, Integer> invokersWeight) {
        if (null != invokersWeight && !invokersWeight.isEmpty()) {
            nodes = new ArrayList<>(invokersWeight.size());
            invokersWeight.forEach((invoker, weight) -> nodes.add(new Node(invoker, weight)));
        } else {
            nodes = null;
        }
    }


    public Invoker select() {
        if (!checkNodes()) {
            return null;
        }
        if (nodes.size() == 1) {
            Invoker invoker = nodes.get(0).invoker;
            return invoker.isAvailable() ? invoker : null;
        }

        Integer total = 0;
        Node nodeOfMaxWeight = null;
        for (Node node : nodes) {
            total += node.effectiveWeight;
            node.currentWeight += node.effectiveWeight;

            if (nodeOfMaxWeight == null) {
                nodeOfMaxWeight = node;
            } else {
                nodeOfMaxWeight = nodeOfMaxWeight.compareTo(node) > 0 ? nodeOfMaxWeight : node;
            }
        }

        nodeOfMaxWeight.currentWeight -= total;
        return nodeOfMaxWeight.invoker;
    }


    public void onInvokeSuccess(Invoker invoker) {
        if (checkNodes()) {
            nodes.stream().filter(node -> invoker.id().equals(node.invoker.id()))
                    .findFirst().get().onInvokerSuccess();
        }
    }

    public void onInvokeFail(Invoker invoker) {
        if (checkNodes()) {
            nodes.stream().filter(node -> invoker.id().equals(node.invoker.id()))
                    .findFirst().get().onInvokeFail();
        }
    }



    private boolean checkNodes() {
        return (null != nodes && nodes.size() > 0);
    }
}
