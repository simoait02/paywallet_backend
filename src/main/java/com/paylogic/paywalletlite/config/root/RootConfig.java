package com.paylogic.paywalletlite.config.root;

import com.paylogic.paywalletlite.config.database.DatabaseMigrationConfig;
import com.paylogic.paywalletlite.config.database.JpaConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "com.paylogic.paywalletlite.service",
        "com.paylogic.paywalletlite.repository",
        "com.paylogic.paywalletlite.security",
        "com.paylogic.paywalletlite.util",
        "com.paylogic.paywalletlite.validation",
        "com.paylogic.paywalletlite.config.database",
        "com.paylogic.paywalletlite.config.security"
})
@Import({
        AppConfig.class,
        PropertyConfig.class,
        JpaConfig.class,
        DatabaseMigrationConfig.class
})
public class RootConfig {
    // Configuration racine - point d'entrée Spring
}