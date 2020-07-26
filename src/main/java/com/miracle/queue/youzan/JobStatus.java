package com.miracle.queue.youzan;

public enum  JobStatus {

    DELAY("delay"),
    READY("ready"),
    RESERVED("reserved"),
    FINISHED("finished"),
    DELETED("deleted")
    ;
    private String status;

    JobStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
