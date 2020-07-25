package com.miracle.queue.wheel;

import com.miracle.queue.entity.Order;
import io.netty.util.HashedWheelTimer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class OrderWheelProducer {

    @PostConstruct
    public void handle() {
        HashedWheelTimer wheelTimer = new HashedWheelTimer(Executors.defaultThreadFactory(), 5, TimeUnit.SECONDS);
        for (int i = 0; i < 10; i++) {
            System.out.println("add timer task");
            OrderTimerTask orderTimerTask = new OrderTimerTask(new Order(i, System.currentTimeMillis()));
            wheelTimer.newTimeout(orderTimerTask, 5 * (i + 1), TimeUnit.SECONDS);
        }
    }


}
