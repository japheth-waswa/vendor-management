package com.vendor.management.system.user.service.domain.valueobject;

import com.vendor.management.system.domain.valueobject.AbstractValueObject;

import java.util.Map;

public class UserAttribute extends AbstractValueObject<Map<Attribute,String>> {
    private Map<Attribute,String> attributes;

    public UserAttribute(Map<Attribute, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<Attribute, String> getValue() {
        return attributes;
    }

    public void setAttributes(Map<Attribute, String> attributes) {
        this.attributes = attributes;
    }
}
