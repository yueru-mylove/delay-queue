package com.miracle.queue.youzan;

import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DelayQueueTimer implements Runnable {

    private Bucket bucket;

    public DelayQueueTimer(Bucket bucket) {
        this.bucket = bucket;
    }

    @Override
    public void run() {
        for (; ; ) {
            if (CollectionUtils.isEmpty(bucket.getBuckets())) {
                return;
            }

            for (int index = 0; index < bucket.getBuckets().size(); index++) {
                Long firstDelayTime = bucket.getFirstDelayTime(index);
                if (firstDelayTime == null || firstDelayTime > LocalDateTime.now().toEpochSecond(ZoneOffset.of("UTC"))) {
                    break;
                }


            }
        }
    }
}
