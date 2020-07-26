package com.miracle.queue.youzan;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitReadyQueueConfig {

    public static final String READY_EXCHANGE = "read.queue.exchange";
    public static final String ORDER_ROUTER = "order.router";

    @Bean
    public Exchange readyQueueExchange() {
        return ExchangeBuilder.topicExchange(READY_EXCHANGE).durable(true).build();
    }


}
