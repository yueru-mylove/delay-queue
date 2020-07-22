package com.miracle.queue.deadletter.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miracle.queue.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.miracle.queue.deadletter.config.OrderMonitorConfig.*;
import static com.miracle.queue.deadletter.config.OrderMonitorConfig.ORDER_TIMEOUT_HANDLER_EXCHANGE;
import static com.miracle.queue.deadletter.config.OrderMonitorConfig.ORDER_TIMEOUT_QUEUE;

@Component
@Slf4j
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;
    private int i = 1;

    @Autowired
    public OrderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 9000)
    public void generatorOrder() {
        try {
            this.rabbitTemplate.convertAndSend(ORDER_TIMEOUT_HANDLER_EXCHANGE, ORDER_PAYING_ROUTING_KEY,
                    new ObjectMapper().writeValueAsString(new Order(++i, System.currentTimeMillis())));
            log.info("send message: [{}]", i);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @RabbitListener(queues = ORDER_TIMEOUT_QUEUE)
    public void handleTimeoutOrder(String content) {
        if (StringUtils.isEmpty(content)) {
            log.info("order is null, skip.");
            return;
        }
        try {
            Order order = new ObjectMapper().readValue(content, Order.class);
            log.info("orderId: [{}], orderTime: [{}]", order.getId(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getOrderTime())));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
