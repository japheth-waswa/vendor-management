package com.vendor.management.system.user.service.domain.valueobject;

public enum CredentialType {
    PASSWORD("password");
    private final String value;

    CredentialType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
