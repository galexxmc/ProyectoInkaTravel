// En: src/main/java/com/inkatravel/config/MvcConfig.java

package com.inkatravel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // Leemos la misma ruta que usamos en el StorageService
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Habilitamos el acceso a la carpeta de 'uploads'
        registry.addResourceHandler("/uploads/**")
                // Mapeamos la URL /uploads/ a la carpeta física 'file:./uploads/'
                .addResourceLocations("file:" + uploadPath + "/");
    }
}