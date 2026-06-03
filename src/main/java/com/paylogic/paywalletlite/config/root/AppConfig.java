package com.paylogic.paywalletlite.config.root;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.annotation.PostConstruct;

@Configuration
@PropertySource({
        "classpath:properties/application.properties",
        "classpath:properties/application-${spring.profiles.active:dev}.properties"
})
public class AppConfig {

    @Value("${jwt.access.secret:NOT_LOADED}")
    private String jwtAccessSecret;

    @PostConstruct
    public void init() {
        System.out.println(">>> AppConfig loaded");
        System.out.println(">>> jwt.access.secret length: " +
                (jwtAccessSecret != null ? jwtAccessSecret.length() : "null") + " chars");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}