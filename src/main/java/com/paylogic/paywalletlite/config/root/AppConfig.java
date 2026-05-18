package com.paylogic.paywalletlite.config.root;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Configuration applicative générale.
 * Définit les beans fondamentaux de l'application.
 */
@Configuration
@PropertySource({
        "classpath:properties/application.properties",
        "classpath:properties/application-${spring.profiles.active:dev}.properties"
})
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}