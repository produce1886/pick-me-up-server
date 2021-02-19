package com.produce.pickmeup.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:3000", "https://pick-me-up.vercel.app:80")
			.allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
			.exposedHeaders("Location");
	}
}