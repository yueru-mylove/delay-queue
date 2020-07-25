package com.miracle.queue.youzan;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHash {

    private TreeMap<Long, String> virtualNodes = new TreeMap<>();
    private LinkedList<String> nodes;
    private final int replicCnt;

    public ConsistentHash(LinkedList<String> nodes, int replicCnt) {
        this.nodes = nodes;
        this.replicCnt = replicCnt;
        initialization();
    }

    private void initialization() {
        for (String node : nodes) {
            for (int i = 0; i < replicCnt / 4; i++) {
                String virtualNodeName = getNodeNameByIndex(node, i);
                for (int j = 0; j < 4; j++) {
                    virtualNodes.put(hash(virtualNodeName, j), node);
                }
            }
        }
    }


    public String selectNode(String key) {
        Long hash = hash(key, 0);
        if (virtualNodes.containsKey(hash)) {
            return virtualNodes.get(hash);
        }
        Map.Entry<Long, String> entry = virtualNodes.ceilingEntry(hash);
        return null == entry ? nodes.getFirst() : entry.getValue();
    }

    public void addNode(String node) {
        nodes.add(node);
        for (int i = 0; i < replicCnt / 4; i++) {
            String virtualNodeName = getNodeNameByIndex(node, i);
            for (int j = 0; j < 4; j++) {
                virtualNodes.put(hash(virtualNodeName, j), node);
            }
        }
    }

    public void removeNode(String node) {
        nodes.remove(node);
        for (int i = 0; i < replicCnt / 4; i++) {
            String virtualNodeName = getNodeNameByIndex(node, 0);
            for (int j = 0; j < 4; j++) {
                virtualNodes.remove(hash(virtualNodeName, j));
            }
        }
    }


    private String getNodeNameByIndex(String nodeName, int index) {
        return nodeName + "&&" + index;
    }


    private byte[] md5(String content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(content.getBytes(StandardCharsets.UTF_8));
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Long hash(String nodeName, int number) {
        byte[] digest = md5(nodeName);
        return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                | ((long) (digest[2 + number * 3] & 0xFF) << 16)
                | ((long) (digest[1 + number * 2] & 0xFF) << 8)
                | (digest[number * 4] & 0xFF))
                & 0xFFFFFFFFL;
    }
}
