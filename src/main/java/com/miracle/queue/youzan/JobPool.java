package com.miracle.queue.youzan;

import com.miracle.queue.youzan.loadbalancer.consitenthash.ConsistentHash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
@Slf4j
public class JobPool {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final ConsistentHash consistentHash;
    private static final LinkedList<String> containers = new LinkedList<>();
    private final Bucket bucket;


    @Autowired
    public JobPool(RedisTemplate<String, Object> redisTemplate, Bucket bucket) {
        this.redisTemplate = redisTemplate;
        this.bucket = bucket;
    }


    public void addJob(Job job) {
        String container = locateContainer(job.getId());
        redisTemplate.opsForHash().put(container, job.getId(), job);
        String bucketName = bucket.push(job.getId(), job.getDelayedTime());
        job.onDelayed(bucketName);
    }


    public Job getJob(Integer id) {
        String container = locateContainer(id);
        return (Job) redisTemplate.opsForHash().get(container, id);
    }

    public void deleteJob(Integer id) {
        String container = locateContainer(id);
        Job job = (Job) redisTemplate.opsForHash().get(container, id);
        if (null == job) {
            log.warn("job: [{}] does not exists.", id);
            return;
        }
        bucket.remove(job.getBucket(), job.getId());
        redisTemplate.opsForHash().delete(container, id);
        job.onDeleted();
    }

    public String locateContainer(Integer id) {
        String idStr = id.toString();
        return consistentHash.selectNode(idStr);
    }


    static {
        for (int i = 0; i < 32; i++) {
            containers.add("container_" + i);
        }
        consistentHash = new ConsistentHash(containers, 1);
    }
}
