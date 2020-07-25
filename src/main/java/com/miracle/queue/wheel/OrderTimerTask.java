package com.miracle.queue.wheel;

import com.miracle.queue.entity.Order;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class OrderTimerTask implements TimerTask {

    private Order order;

    public OrderTimerTask(Order order) {
        this.order = order;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (timeout.isCancelled() || timeout.isExpired()) {
            log.info("order: [{}] is cancelled or expired.", order);
            timeout.timer().newTimeout(this, 5, TimeUnit.SECONDS);
            return;
        }
        System.out.println("开始处理订单：" + order);
        log.info("开始处理订单：[{}]", order);
    }


    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
