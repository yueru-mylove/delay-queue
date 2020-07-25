package com.miracle.queue.youzan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;

@Component
public class JobPool {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final ConsistentHash consistentHash;
    private static final LinkedList<String> containers = new LinkedList<>();


    @Autowired
    public JobPool(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void addJob(Job job) {
        String container = locateContainer(job.getId());
        redisTemplate.opsForHash().put(container, job.getId(), job);
    }


    public Job getJob(Integer id) {
        String container = locateContainer(id);
        return (Job) redisTemplate.opsForHash().get(container, id);
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
