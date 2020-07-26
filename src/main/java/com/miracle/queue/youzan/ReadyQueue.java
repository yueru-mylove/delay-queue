package com.miracle.queue.youzan;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReadyQueue {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ReadyQueue(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicReadyJob(Job job) {
        String topic = job.getTopic();
        rabbitTemplate.convertAndSend(RabbitReadyQueueConfig.READY_EXCHANGE, topic, job);
    }


}
