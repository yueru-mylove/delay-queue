package com.miracle.queue.youzan;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class Job {

    private String topic;

    private Integer id;

    private Long delay = 0L;

    /**
     * job execution timeout
     *
     * time-to-run
     */
    private Long ttr = 30L;

    private String bucket;

    private JobStatus status;

    private Object body;


    public Long getDelayedTime() {
        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(delay);
        return localDateTime.toEpochSecond(ZoneOffset.of("+0"));
    }

    public Long getTtrTime() {
        return getDelayedTime() + ttr;
    }


    private boolean expired() {
        return false;
    }

    public void onDelayed(String bucketName) {
        this.bucket = bucketName;
        this.status = JobStatus.DELAY;
    }

    public void onReady() {
        this.status = JobStatus.READY;
    }

    public void onReserved() {
        this.status = JobStatus.RESERVED;
    }

    public void onDeleted() {
        this.status = JobStatus.DELETED;
    }


}
