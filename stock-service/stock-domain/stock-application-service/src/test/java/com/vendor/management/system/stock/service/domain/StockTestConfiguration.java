package com.vendor.management.system.stock.service.domain;

import com.vendor.management.system.stock.service.domain.ports.output.repository.*;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.vendor.management.system")
public class StockTestConfiguration {

    @Bean
    public StockDomainService stockDomainService() {
        return new StockDomainServiceImpl();
    }

    @Bean
    public ProductCategoryRepository productCategoryRepository() {
        return Mockito.mock(ProductCategoryRepository.class);
    }

    @Bean
    public ProductRepository productRepository() {
        return Mockito.mock(ProductRepository.class);
    }

    @Bean
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public OrderFileOutboxRepository orderFileOutboxRepository() {
        return Mockito.mock(OrderFileOutboxRepository.class);
    }

    @Bean
    public OrderFinanceOutboxRepository orderFinanceOutboxRepository() {
        return Mockito.mock(OrderFinanceOutboxRepository.class);
    }
}
