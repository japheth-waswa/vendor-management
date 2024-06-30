package com.vendor.management.system.stock.service.domain;

import com.vendor.management.system.domain.valueobject.ConjurDatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static com.vendor.management.system.domain.util.DomainConstants.*;

@Configuration
public class DatabaseConfig {
    private final ConjurDatabaseConfig conjurDatabaseConfig;

    public DatabaseConfig(ConjurDatabaseConfig conjurDatabaseConfig) {
        this.conjurDatabaseConfig = conjurDatabaseConfig;
    }

    @Bean
    public DataSource getDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class)
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://" + conjurDatabaseConfig.conjurHost().variables().retrieveSecret(CONJUR_DATABASE_CONFIG_POSTGRESQL_HOST)
                        + "/" + conjurDatabaseConfig.conjurHost().variables().retrieveSecret(CONJUR_DATABASE_CONFIG_POSTGRESQL_DATABASE)
                        + "?currentSchema=" + STOCK_SCHEMA_NAME + "&binaryTransfer=true&reWriteBatchedInserts=true&stringtype=unspecified")
                .username(conjurDatabaseConfig.conjurHost().variables().retrieveSecret(CONJUR_DATABASE_CONFIG_POSTGRESQL_USER_NAME))
                .password(conjurDatabaseConfig.conjurHost().variables().retrieveSecret(CONJUR_DATABASE_CONFIG_POSTGRESQL_PASSWORD))
                .build();
    }
}
