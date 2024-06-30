package com.vendor.management.system.outbox.model;

import java.util.UUID;

public abstract class OutboxMessage{
    public abstract UUID getOutboxMessageId();
}
