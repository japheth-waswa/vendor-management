package com.vendor.management.system.user.service.domain.valueobject;

import com.vendor.management.system.domain.valueobject.AbstractValueObject;

public class Username extends AbstractValueObject<String> {
    private final String value;

    public Username(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
