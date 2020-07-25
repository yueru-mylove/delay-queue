package com.miracle.queue.youzan;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashWithoutVirtualNode {

    private TreeMap<Long, String> realNodes = new TreeMap<>();
    private String[] nodes;

    public ConsistentHashWithoutVirtualNode(String[] nodes) {
        this.realNodes = realNodes;
        this.nodes = Arrays.copyOf(nodes, nodes.length);
    }


    private void initialization() {
        for (String node : nodes) {
            realNodes.put(hash(node, 0), node);
        }
    }


    public String selectNode(String key) {
        Long hash = hash(key, 0);
        if (realNodes.containsKey(hash)) {
            return realNodes.get(hash);
        }
        Map.Entry<Long, String> entry = realNodes.ceilingEntry(hash);
        if (entry != null) {
            return entry.getValue();
        } else {
            return nodes[0];
        }
    }


    private Long hash(String node, int number) {
        byte[] digest = md5(node);
        return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                | (digest[number * 4] & 0xFF))
                & 0xFFFFFFFFL;

    }


    public byte[] md5(String content) {

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(content.getBytes("UTF-8"));
            return md5.digest();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
    }
}
