package com.miracle.queue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Administrator
 */
@SpringBootApplication
@EnableScheduling
public class DelayQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(DelayQueueApplication.class, args);
    }
}
