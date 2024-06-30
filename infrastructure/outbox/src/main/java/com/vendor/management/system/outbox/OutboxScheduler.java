package com.vendor.management.system.outbox;

public interface OutboxScheduler {
    void processOutboxMessage();
}
