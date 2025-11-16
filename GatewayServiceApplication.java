package com.tpi.logistica.gateway_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration; // Importar

// <--- CLAVE: AGREGAR ESTA EXCLUSIÓN --->
@SpringBootApplication(exclude = {ReactiveSecurityAutoConfiguration.class})
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}