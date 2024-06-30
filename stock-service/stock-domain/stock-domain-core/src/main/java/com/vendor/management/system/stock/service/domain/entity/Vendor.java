package com.vendor.management.system.stock.service.domain.entity;

import com.vendor.management.system.domain.entity.AggregateRoot;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.domain.valueobject.VendorName;

public class Vendor extends AggregateRoot<VendorId> {
    private final VendorName vendorName;

    public Vendor(VendorName vendorName) {
        this.vendorName = vendorName;
    }

    public VendorName getVendorName() {
        return vendorName;
    }
}
