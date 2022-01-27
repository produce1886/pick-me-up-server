package com.produce.pickmeup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000", "https://pickmeup.site", "https://pick-me-up.vercel.app")
            .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
            .exposedHeaders("Location");
    }
}
