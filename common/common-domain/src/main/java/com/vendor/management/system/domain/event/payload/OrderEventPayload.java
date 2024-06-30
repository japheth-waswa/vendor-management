package com.vendor.management.system.domain.event.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Builder
@AllArgsConstructor
public class OrderEventPayload extends EventPayload{
    @JsonProperty
    private String id;
    @JsonProperty
    private String sagaId;
    @JsonProperty
    private String orderId;
    @JsonProperty
    private String customerId;
    @JsonProperty
    private BigDecimal price;
    @JsonProperty
    private ZonedDateTime createdAt;
    @JsonProperty
    private String orderStatus;

    @Override
    public ZonedDateTime getCreatedAtDate() {
        return createdAt;
    }

    @Override
    public String getEventName() {
        return "Order";
    }

    @Override
    public String getEventId() {
        return id;
    }
}
