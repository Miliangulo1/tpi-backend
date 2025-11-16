package com.tpi.logistica.ms_transporte.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    /**
     * Bean de RestClient que apunta a la URL base de ms_flota.
     */
    @Bean
    public RestClient flotaRestClient(@Value("${msflota.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * === ¡NUEVO BEAN! ===
     * Bean de RestClient que apunta a la URL base de ms_tarifas.
     [cite_start]* [cite: 155-162]
     */
    @Bean
    public RestClient tarifasRestClient(@Value("${mstarifas.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}