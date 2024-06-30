package com.vendor.management.system.domain.util;

public final class DomainConstants {
    private DomainConstants() {
    }

    public static final String UTC = "UTC";
    public static final String Order_Not_Found_Exception_Message = "Order not available!";
    public static final String Orders_Not_Found_Exception_Message = "Orders not found!";
    public static final String FAILURE_MESSAGE_DELIMITER = ",";
    public static final String ORDER_SAGA_NAME = "OrderProcessingSaga";
    public static final String STOCK_SCHEMA_NAME="stock";

    //conjur database config variables
    public static final String CONJUR_DATABASE_CONFIG_POSTGRESQL_HOST="DatabaseConfig/postgresqlHost";
    public static final String CONJUR_DATABASE_CONFIG_POSTGRESQL_DATABASE="DatabaseConfig/postgresqlDatabase";
    public static final String CONJUR_DATABASE_CONFIG_POSTGRESQL_USER_NAME="DatabaseConfig/postgresqlUsername";
    public static final String CONJUR_DATABASE_CONFIG_POSTGRESQL_PASSWORD="DatabaseConfig/postgresqlPassword";

    //conjur user service variables
    public static final String CONJUR_USER_SERVICE_KEYCLOAK_BASE_URL="UserServiceMicroservice/keycloakBaseUrl";
    public static final String CONJUR_USER_SERVICE_KEYCLOAK_CLIENT_ID="UserServiceMicroservice/keycloakClientId";
    public static final String CONJUR_USER_SERVICE_KEYCLOAK_CLIENT_SECRET="UserServiceMicroservice/keycloakClientSecret";
}