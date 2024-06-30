package com.vendor.management.system.domain.valueobject;

public enum UserField {
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email");
    private final String value;

    UserField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
