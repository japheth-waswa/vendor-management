package com.vendor.management.system.domain.valueobject;

import java.util.HashMap;
import java.util.Map;

public enum Role {
    SU_ADMIN("super_admin"),
    ADMIN("normal_admin"),
    VENDOR("vendor"),
    VENDOR_USER("vendor_user"),
    CUSTOMER("customer"),
    GUEST("guest");
    private final String value;

    private static final Map<String,Role> VALUE_TO_ROLE_MAP=new HashMap<>();

    static {
        for(Role role:Role.values()){
            VALUE_TO_ROLE_MAP.put(role.getValue(),role);
        }
    }

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean containsValue(String value) {
        return VALUE_TO_ROLE_MAP.containsKey(value);
    }

    public static Role extractRole(String value) {
        return VALUE_TO_ROLE_MAP.get(value);
    }
}
