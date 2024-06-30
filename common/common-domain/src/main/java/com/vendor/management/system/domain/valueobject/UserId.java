package com.vendor.management.system.domain.valueobject;

import com.vendor.management.system.domain.exception.DomainException;

public class UserId extends BaseId<String>{
    public UserId(String value) {
        super(value);
        if(value == null || value.isBlank()){
            throw new DomainException("User id cannot be null");
        }
    }
}
