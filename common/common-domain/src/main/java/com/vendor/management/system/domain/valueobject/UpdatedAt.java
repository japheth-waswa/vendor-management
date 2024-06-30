package com.vendor.management.system.domain.valueobject;

import com.vendor.management.system.domain.exception.DomainException;

import java.time.ZonedDateTime;

public class UpdatedAt extends AbstractValueObject<ZonedDateTime> {
    private final ZonedDateTime value;

    public UpdatedAt(ZonedDateTime value) {
        if (value == null) throw new DomainException("UpdatedAt is required");
        this.value = value;
    }

    @Override
    public ZonedDateTime getValue() {
        return value;
    }
}
