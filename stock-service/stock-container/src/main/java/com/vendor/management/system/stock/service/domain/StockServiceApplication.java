package com.vendor.management.system.stock.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.vendor.management.system.stock.service.dataaccess", "com.vendor.management.system.dataaccess"})
@EntityScan(basePackages = {"com.vendor.management.system.stock.service.dataaccess", "com.vendor.management.system.dataaccess"})
@SpringBootApplication(scanBasePackages = "com.vendor.management.system")
public class StockServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockServiceApplication.class, args);
    }
}
