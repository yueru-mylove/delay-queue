package com.miracle.queue.deadletter.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OrderMonitorConfig {

    private final RabbitTemplate rabbitTemplate;
    public static final String ORDER_TIMEOUT_HANDLER_EXCHANGE = "order.timeout.exchange";
    public static final String ORDER_PAYING_QUEUE = "order.paying.queue";
    public static final String ORDER_TIMEOUT_QUEUE = "order.timeout.queue";
    public static final String ORDER_PAYING_ROUTING_KEY = "order.paying";
    private static final Integer ORDER_TIMEOUT = 10;
    private static final String ORDER_TIMEOUT_ROUTING_KEY = "order.timeout";

    @Autowired
    public OrderMonitorConfig(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Bean
    public TopicExchange orderTimeoutExchange() {
        return ExchangeBuilder.topicExchange(ORDER_TIMEOUT_HANDLER_EXCHANGE).build();
    }

    @Bean
    public Queue orderPayingQueue() {
        Map<String, Object> args = new HashMap<>(2);
//       x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", ORDER_TIMEOUT_HANDLER_EXCHANGE);
//       x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", ORDER_TIMEOUT_ROUTING_KEY);
        args.put("x-message-ttl", ORDER_TIMEOUT);
        return QueueBuilder.durable(ORDER_PAYING_QUEUE).withArguments(args).build();
    }

    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(ORDER_TIMEOUT_QUEUE).build();
    }

    @Bean
    public Binding payingQueueBinding() {
        return BindingBuilder.bind(orderPayingQueue()).to(orderTimeoutExchange()).with(ORDER_PAYING_ROUTING_KEY);
    }

    @Bean
    public Binding timeoutQueueBinding() {
        return BindingBuilder.bind(orderTimeoutQueue()).to(orderTimeoutExchange()).with(ORDER_TIMEOUT_ROUTING_KEY);
    }


}
