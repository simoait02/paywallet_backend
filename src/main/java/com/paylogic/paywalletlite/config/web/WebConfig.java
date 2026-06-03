package com.paylogic.paywalletlite.config.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.List;
import java.util.TimeZone;

/**
 * Configuration Spring MVC Web.
 * Active le support REST avec JSON comme format par défaut.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "com.paylogic.paywalletlite.controller",
})
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure la négociation de contenu - JSON par défaut
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }

    /**
     * Gestion des ressources statiques (documentation, etc.)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("/static/");
    }

    /**
     * Configuration globale Jackson ObjectMapper.
     * Supporte Java Time API (LocalDateTime, LocalDate, Instant, etc.)
     */
    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        // Support Java 8+ Date/Time API
        mapper.registerModule(new JavaTimeModule());

        // Désactive les timestamps numériques
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Timezone globale application
        mapper.setTimeZone(TimeZone.getTimeZone("Africa/Casablanca"));

        return mapper;
    }

    /**
     * Converter JSON personnalisé utilisant notre ObjectMapper
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        MappingJackson2HttpMessageConverter jsonConverter =
                new MappingJackson2HttpMessageConverter();

        jsonConverter.setObjectMapper(objectMapper());

        converters.add(jsonConverter);
    }

    /**
     * Vue JSON par défaut pour les réponses REST
     */
    @Bean
    public MappingJackson2JsonView jsonView() {

        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();

        jsonView.setObjectMapper(objectMapper());

        return jsonView;
    }

    /**
     * Permet au servlet par défaut de gérer les ressources statiques
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}