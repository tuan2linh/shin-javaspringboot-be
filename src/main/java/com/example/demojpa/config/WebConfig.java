package com.example.demojpa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL /uploads/** => Folder uploads/ trong project
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
