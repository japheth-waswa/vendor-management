package com.vendor.management.system.domain.valueobject;

public class FileUrl extends AbstractValueObject<String>{
    private final String url;

    public FileUrl(String url) {
        this.url = url;
    }

    @Override
    public String getValue() {
        return url;
    }
}
