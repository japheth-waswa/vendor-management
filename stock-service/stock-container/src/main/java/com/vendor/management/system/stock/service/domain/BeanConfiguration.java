package com.vendor.management.system.stock.service.domain;

import com.vendor.management.system.dataaccess.util.DataAccessHelper;
import com.vendor.management.system.domain.util.YamlPropertySourceFactory;
import com.vendor.management.system.stock.service.dataaccess.order.adapter.OrderRepositoryImpl;
import com.vendor.management.system.stock.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.vendor.management.system.stock.service.dataaccess.order.repository.OrderItemJpaRepository;
import com.vendor.management.system.stock.service.dataaccess.order.repository.OrderJpaRepository;
import com.vendor.management.system.stock.service.dataaccess.outbox.common.mapper.OrderOutboxDataAccessMapper;
import com.vendor.management.system.stock.service.dataaccess.outbox.file.adapter.OrderFileOutboxRepositoryImpl;
import com.vendor.management.system.stock.service.dataaccess.outbox.file.repository.OrderFileOutboxJpaRepository;
import com.vendor.management.system.stock.service.dataaccess.outbox.finance.adapter.OrderFinanceOutboxRepositoryImpl;
import com.vendor.management.system.stock.service.dataaccess.outbox.finance.repository.OrderFinanceOutboxJpaRepository;
import com.vendor.management.system.stock.service.dataaccess.product.adapter.ProductRepositoryImpl;
import com.vendor.management.system.stock.service.dataaccess.product.mapper.ProductDataAccessMapper;
import com.vendor.management.system.stock.service.dataaccess.product.repository.ProductJpaRepository;
import com.vendor.management.system.stock.service.dataaccess.productcategory.adapter.ProductCategoryRepositoryImpl;
import com.vendor.management.system.stock.service.dataaccess.productcategory.mapper.ProductCategoryDataAccessMapper;
import com.vendor.management.system.stock.service.dataaccess.productcategory.repository.ProductCategoryJpaRepository;
import com.vendor.management.system.stock.service.domain.ports.output.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource(value = "classpath:stock-container-application.yml", factory = YamlPropertySourceFactory.class)
})
public class BeanConfiguration {
    private final ProductCategoryJpaRepository productCategoryJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final OrderFileOutboxJpaRepository orderFileOutboxJpaRepository;
    private final OrderFinanceOutboxJpaRepository orderFinanceOutboxJpaRepository;

    public BeanConfiguration(ProductCategoryJpaRepository productCategoryJpaRepository,
                             ProductJpaRepository productJpaRepository,
                             OrderItemJpaRepository orderItemJpaRepository,
                             OrderJpaRepository orderJpaRepository,
                             OrderFileOutboxJpaRepository orderFileOutboxJpaRepository,
                             OrderFinanceOutboxJpaRepository orderFinanceOutboxJpaRepository
    ) {
        this.productCategoryJpaRepository = productCategoryJpaRepository;
        this.productJpaRepository = productJpaRepository;
        this.orderItemJpaRepository = orderItemJpaRepository;
        this.orderJpaRepository = orderJpaRepository;
        this.orderFileOutboxJpaRepository = orderFileOutboxJpaRepository;
        this.orderFinanceOutboxJpaRepository = orderFinanceOutboxJpaRepository;
    }

    @Bean
    public StockDomainService stockDomainService() {
        return new StockDomainServiceImpl();
    }

    @Bean
    public ProductCategoryDataAccessMapper productCategoryDataAccessMapper() {
        return new ProductCategoryDataAccessMapper();
    }

    @Bean
    public ProductDataAccessMapper productDataAccessMapper() {
        return new ProductDataAccessMapper();
    }

    @Bean
    public OrderDataAccessMapper orderDataAccessMapper() {
        return new OrderDataAccessMapper(productDataAccessMapper());
    }

    @Bean
    public DataAccessHelper dataAccessHelper() {
        return new DataAccessHelper();
    }

    @Bean
    public OrderOutboxDataAccessMapper orderOutboxDataAccessMapper() {
        return new OrderOutboxDataAccessMapper();
    }

    @Bean
    public ProductCategoryRepository productCategoryRepository() {
        return new ProductCategoryRepositoryImpl(productCategoryJpaRepository, productCategoryDataAccessMapper(), dataAccessHelper());
    }

    @Bean
    public ProductRepository productRepository() {
        return new ProductRepositoryImpl(productJpaRepository, productDataAccessMapper(), dataAccessHelper());
    }

    @Bean
    public OrderRepository orderRepository() {
        return new OrderRepositoryImpl(orderJpaRepository, orderItemJpaRepository, orderDataAccessMapper(), dataAccessHelper());
    }

    @Bean
    public OrderFileOutboxRepository outboxRepository() {
        return new OrderFileOutboxRepositoryImpl(orderFileOutboxJpaRepository, orderOutboxDataAccessMapper());
    }

    @Bean
    public OrderFinanceOutboxRepository orderFinanceOutboxRepository() {
        return new OrderFinanceOutboxRepositoryImpl(orderFinanceOutboxJpaRepository, orderOutboxDataAccessMapper());
    }
}
