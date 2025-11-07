package com.inkatravel.config;

import jakarta.annotation.PostConstruct;
import com.mercadopago.MercadoPagoConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoSdkConfig {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        // Inicializa el SDK de Mercado Pago con nuestro Access Token
        MercadoPagoConfig.setAccessToken(accessToken);
    }
}