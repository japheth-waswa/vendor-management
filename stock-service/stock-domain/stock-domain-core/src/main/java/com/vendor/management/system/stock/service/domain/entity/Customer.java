package com.vendor.management.system.stock.service.domain.entity;

import com.vendor.management.system.domain.entity.AggregateRoot;
import com.vendor.management.system.domain.valueobject.Address;
import com.vendor.management.system.domain.valueobject.CustomerId;
import com.vendor.management.system.domain.valueobject.Names;

public class Customer extends AggregateRoot<CustomerId> {
    private final Names names;
    private final Address address;

    public Customer(Names names, Address address) {
        this.names = names;
        this.address = address;
    }

    public Names getNames() {
        return names;
    }

    public Address getAddress() {
        return address;
    }
}
