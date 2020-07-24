package com.miracle.queue.redislistener;

import com.miracle.queue.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
@Component
@Slf4j
public class RabbitListenerOrderProducer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        producerOrder();
    }
    
    private void producerOrder() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            redisTemplate.delete("order_" + i);
        }

        for (int i = 0; i < 100; i++) {
            long second = LocalDateTime.now().plusSeconds(random.nextInt(200)).toEpochSecond(ZoneOffset.of("+8"));
            Order order = new Order(i, second);
            long timeout = i + 1;
            redisTemplate.opsForValue().set("order_" + i, order, timeout, TimeUnit.SECONDS);
        }
    }


    public static void main(String[] args) {


        for (int i = 0; i < 100; i++) {
            System.out.println(new Random().nextInt(20));
        }
    }
}
