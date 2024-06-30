package com.vendor.management.system.domain.valueobject;

public class Quantity extends AbstractValueObject<Integer>{
    private final int value;

    public Quantity(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
