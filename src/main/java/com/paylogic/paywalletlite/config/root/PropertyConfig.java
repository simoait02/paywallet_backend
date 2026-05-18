package com.paylogic.paywalletlite.config.root;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * Configuration avancée des propriétés.
 * Permet d'ajouter des propriétés dynamiques ou calculées.
 */
@Configuration
public class PropertyConfig implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Propriétés calculées ou dérivées.
     */
    @Bean
    public PropertiesPropertySource derivedProperties() {
        Properties props = new Properties();

        // Exemple : propriété dérivée
        String appName = environment.getProperty("app.name", "PayWallet Lite");
        String appVersion = environment.getProperty("app.version", "1.0.0");
        props.setProperty("app.fullName", appName + " v" + appVersion);

        // Propriété conditionnelle
        boolean isDev = "dev".equals(environment.getProperty("app.environment"));
        props.setProperty("app.debugMode", String.valueOf(isDev));

        return new PropertiesPropertySource("derivedProperties", props);
    }
}