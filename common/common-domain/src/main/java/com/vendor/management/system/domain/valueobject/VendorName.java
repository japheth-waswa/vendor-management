package com.vendor.management.system.domain.valueobject;

import com.vendor.management.system.domain.exception.DomainException;

public class VendorName extends AbstractValueObject<String>{
    private final String name;

    public VendorName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("Vendor name is required");
        }
        this.name = name;
    }

    @Override
    public String getValue() {
        return name;
    }
}
