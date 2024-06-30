package com.vendor.management.system.user.service.domain.valueobject;

import java.util.HashMap;
import java.util.Map;

public enum Attribute {
    VENDOR_ID("createdByVendorId");
    private final String value;

    private static final Map<String, Attribute> VALUE_TO_ATTRIBUTE_MAP = new HashMap<>();

    static {
        for (Attribute attribute : Attribute.values()) {
            VALUE_TO_ATTRIBUTE_MAP.put(attribute.getValue(), attribute);
        }
    }

    Attribute(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean containsValue(String value) {
        return VALUE_TO_ATTRIBUTE_MAP.containsKey(value);
    }

    public static Attribute extractAttribute(String value) {
        return VALUE_TO_ATTRIBUTE_MAP.get(value);
    }
}
