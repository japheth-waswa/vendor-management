package com.vendor.management.system.domain.valueobject;

import com.vendor.management.system.domain.exception.DomainException;

import java.util.UUID;

public class VendorId extends BaseId<UUID> {
    public VendorId(UUID value) {
        super(value);
        if(value == null){
            throw new DomainException("Vendor id is required!");
        }
    }
}
