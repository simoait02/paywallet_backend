package com.paylogic.paywalletlite.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration CORS (Cross-Origin Resource Sharing).
 * Permet les requêtes depuis l'application mobile Flutter.
 */
@Configuration
public class CORSConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées (Flutter mobile + debug)
        configuration.setAllowedOrigins(List.of(
                "http://localhost",
                "http://localhost:8080",
                "capacitor://localhost",  // Ionic/Capacitor
                "ionic://localhost",
                "http://localhost:8100"   // Ionic dev server
        ));

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));

        // Exposition des headers
        configuration.setExposedHeaders(List.of("Authorization", "X-Auth-Token"));

        // Credentials (cookies, auth)
        configuration.setAllowCredentials(true);

        // Durée du cache preflight
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}