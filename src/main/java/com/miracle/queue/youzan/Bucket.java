package com.miracle.queue.youzan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Bucket {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final AtomicInteger index = new AtomicInteger(0);

    private List<String> buckets = new ArrayList<>();
    private static final int bucketSize = 1 << 6;

    @Autowired
    public Bucket(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * create buckets, just for test
     */
    @PostConstruct
    public void init() {
        for (int i = 0; i < 32; i++) {
            buckets.add("buckets_" + i);
        }
    }


    public String push(Integer jobId, Long delayedTime) {
        String bucket = locateCurrentBucket();
        redisTemplate.opsForZSet().add(bucket, jobId, delayedTime);
        return bucket;
    }


    public void remove(String bucket, Integer jobId) {
        redisTemplate.opsForZSet().remove(bucket, jobId);
    }


    /**
     * 获得桶名称
     *
     * @return
     */
    public String locateCurrentBucket() {
        int currentIndex = index.addAndGet(1);
        int index = currentIndex % buckets.size();
        return buckets.get(index);
    }


    public Long getFirstDelayTime(Integer bucketIndex) {
        String bucket = buckets.get(bucketIndex);
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().rangeWithScores(bucket, 0, 1);
        if (null == typedTuples || typedTuples.isEmpty()) {
            return null;
        }

        return typedTuples.iterator().next().getScore().longValue();
    }

    public static int getBucketSize() {
        return bucketSize;
    }

    public List<String> getBuckets() {
        return buckets;
    }
}
