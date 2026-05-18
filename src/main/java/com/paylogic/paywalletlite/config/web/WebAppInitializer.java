package com.paylogic.paywalletlite.config.web;

import com.paylogic.paywalletlite.config.root.RootConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        /*
         * ============================================================
         * ROOT APPLICATION CONTEXT
         * Services, repositories, security, database...
         * ============================================================
         */
        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();

        rootContext.register(RootConfig.class);

        servletContext.addListener(
                new ContextLoaderListener(rootContext)
        );

        /*
         * ============================================================
         * WEB MVC CONTEXT
         * Controllers REST + Spring MVC
         * ============================================================
         */
        AnnotationConfigWebApplicationContext webContext =
                new AnnotationConfigWebApplicationContext();

        webContext.register(WebConfig.class);

        DispatcherServlet dispatcherServlet =
                new DispatcherServlet(webContext);

        ServletRegistration.Dynamic dispatcher =
                servletContext.addServlet("dispatcher", dispatcherServlet);

        dispatcher.setLoadOnStartup(1);

        dispatcher.addMapping("/api/*");

        /*
         * ============================================================
         * UTF-8 ENCODING FILTER
         * ============================================================
         */
        CharacterEncodingFilter encodingFilter =
                new CharacterEncodingFilter();

        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);

        servletContext.addFilter("encodingFilter", encodingFilter)
                .addMappingForUrlPatterns(null, false, "/*");

        /*
         * ============================================================
         * SESSION CONFIG
         * ============================================================
         */
        servletContext.setSessionTimeout(30);
    }
}