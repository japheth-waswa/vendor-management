package com.vendor.management.system.user.service.domain.valueobject;

public class UserCredential {
    private final CredentialType credentialType;
    private final String value;
    private final boolean temporary;

    public UserCredential(CredentialType credentialType, String value, boolean temporary) {
        this.credentialType = credentialType;
        this.value = value;
        this.temporary = temporary;
    }

    public CredentialType getCredentialType() {
        return credentialType;
    }

    public String getValue() {
        return value;
    }

    public boolean isTemporary() {
        return temporary;
    }
}
