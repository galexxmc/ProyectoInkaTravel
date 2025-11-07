package com.inkatravel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * Crea un Bean de RestTemplate para que podamos
     * inyectarlo en nuestros servicios y hacer llamadas HTTP.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}