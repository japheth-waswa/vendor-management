package com.vendor.management.system.domain.valueobject;

public class Status extends AbstractValueObject<Boolean>{
    private final boolean status;

    public Status(boolean status) {
        this.status = status;
    }

    @Override
    public Boolean getValue() {
        return status;
    }
}
