package com.tpi.logistica.ms_transporte.client;

import com.tpi.logistica.ms_transporte.dto.TarifaDTO;
// ¡¡NUEVO IMPORT!!
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.util.List;

@Component
public class TarifasApiClient {

    private final RestClient restClient;

    public TarifasApiClient(RestClient tarifasRestClient) {
        this.restClient = tarifasRestClient;
    }

    public List<TarifaDTO> getAllTarifas() {
        try {
            return restClient.get()
                    .uri("/api/v1/tarifas")
                    .retrieve()
                    // ¡¡CAMBIO AQUÍ!!
                    // Usamos ParameterizedTypeReference para capturar
                    // el tipo genérico List<TarifaDTO>
                    .body(new ParameterizedTypeReference<List<TarifaDTO>>() {});

        } catch (RestClientResponseException ex) {
            System.err.println("Error al consultar ms_tarifas: " + ex.getMessage());
            return List.of();
        }
    }
}