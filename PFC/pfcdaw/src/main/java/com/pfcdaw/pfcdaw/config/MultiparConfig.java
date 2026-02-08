package com.pfcdaw.pfcdaw.config;


import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class MultiparConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory multipartConfig = new MultipartConfigFactory();
        // Limites iguales que application.properties (5MB)
        multipartConfig.setMaxFileSize(DataSize.ofMegabytes(5)); // Limita o tamaño de cada arquivo a 5MB
        multipartConfig.setMaxRequestSize(DataSize.ofMegabytes(5)); // Limita o tamaño total da solicitude a 5MB
        return multipartConfig.createMultipartConfig();
    }

}
