package com.vendor.management.system.user.service.domain;

import com.vendor.management.system.domain.util.YamlPropertySourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource(value = "classpath:user-dataaccess-application.yml", factory = YamlPropertySourceFactory.class),
        @PropertySource(value = "classpath:user-container-application.yml", factory = YamlPropertySourceFactory.class)
})
public class BeanConfiguration {

    @Bean
    public UserDomainService userDomainService() {
        return new UserDomainServiceImpl();
    }
}
