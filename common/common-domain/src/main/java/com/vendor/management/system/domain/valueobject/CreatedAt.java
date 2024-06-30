package com.vendor.management.system.domain.valueobject;

import com.vendor.management.system.domain.exception.DomainException;

import java.time.ZonedDateTime;

public class CreatedAt extends AbstractValueObject<ZonedDateTime> {
    private final ZonedDateTime value;

    public CreatedAt(ZonedDateTime value) {
        if (value == null) throw new DomainException("CreatedAt is required");
        this.value = value;
    }

    @Override
    public ZonedDateTime getValue() {
        return value;
    }
}
