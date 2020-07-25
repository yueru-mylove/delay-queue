package com.miracle.queue.wheel;

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

public class OrderTimeout implements Timeout {
    @Override
    public Timer timer() {
        return null;
    }

    @Override
    public TimerTask task() {
        return null;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean cancel() {
        return false;
    }
}
