package com.miracle.queue.zset;

import com.miracle.queue.entity.Order;
import com.miracle.queue.pool.TraceThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Set;

@Component
@Slf4j
public class RedisOrderProducer {

    public static final String ORDER_MONITOR_SET = "order_monitor_set";

    private final RedisTemplate<String, Object> redisTemplate;
    private int orderId = 1;

    @Autowired
    public RedisOrderProducer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Scheduled(fixedRate = 5000)
    public void orderProducer() {
        for (int i = 0; i < 5; i++) {
            redisTemplate.opsForZSet().add(ORDER_MONITOR_SET, new Order(++orderId, System.currentTimeMillis()), System.currentTimeMillis() + i * 1000);
        }
        redisTemplate.opsForZSet().add(ORDER_MONITOR_SET, new Order(++orderId, System.currentTimeMillis()), System.currentTimeMillis());
    }


    @Scheduled(fixedRate = 1000)
    public void orderMonitor() {
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().rangeWithScores(ORDER_MONITOR_SET, 0, 10);
        if (CollectionUtils.isEmpty(typedTuples)) {
            log.info("no order waiting to pay...");
            return;
        }

        Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Object> next = iterator.next();
            Order order = (Order) next.getValue();
            System.out.println(order);
            Long score = next.getScore().longValue();
            if (score > System.currentTimeMillis()) {
                redisTemplate.opsForZSet().remove(ORDER_MONITOR_SET, order);
            }
        }
    }

}
