package com.vendor.management.system.stock.service.domain.outbox.scheduler.file;

import com.vendor.management.system.outbox.OutboxScheduler;
import com.vendor.management.system.outbox.OutboxStatus;
import com.vendor.management.system.outbox.model.OrderOutboxMessage;
import com.vendor.management.system.saga.SagaStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class FileOutboxCleanerScheduler implements OutboxScheduler {
    private final FileOutboxHelper fileOutboxHelper;

    public FileOutboxCleanerScheduler(FileOutboxHelper fileOutboxHelper) {
        this.fileOutboxHelper = fileOutboxHelper;
    }

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        Optional<List<OrderOutboxMessage>> outboxMessages = fileOutboxHelper
                .getOrderOutboxMessageByOutboxStatusAndSagaStatus(
                        OutboxStatus.COMPLETED,
                        SagaStatus.SUCCEEDED,
                        SagaStatus.FAILED,
                        SagaStatus.COMPENSATED);
        if(outboxMessages.isEmpty())return;
        List<OrderOutboxMessage> messages  = outboxMessages.get();
        log.info("Received {} File OrderOutboxMessage for clean-up. The payloads: {}",
                messages.size(),messages.stream()
                        .map(OrderOutboxMessage::getPayload)
                        .reduce((a,b)->a+"\n"+b).orElse(""));
        fileOutboxHelper.deleteOrderOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED,
                SagaStatus.SUCCEEDED,
                SagaStatus.FAILED,
                SagaStatus.COMPENSATED);
        log.info("{} File OrderOutboxMessage deleted!",messages.size());
    }
}
