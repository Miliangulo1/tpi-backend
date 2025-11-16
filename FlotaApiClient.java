package com.tpi.logistica.ms_transporte.client;

import com.tpi.logistica.ms_transporte.dto.CamionDTO; // El DTO que ya creamos
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException; // Para manejo de errores

import java.util.List;

@Component // Le dice a Spring que cree una instancia de esta clase
public class FlotaApiClient {

    // Inyectamos el Bean 'flotaRestClient' que creamos en RestClientConfig
    private final RestClient restClient;

    // Spring inyecta automáticamente el bean que coincide con el tipo y nombre
    public FlotaApiClient(RestClient flotaRestClient) {
        this.restClient = flotaRestClient;
    }

    /**
     * Llama al endpoint 'GET /api/v1/flota/camiones/{id}' de ms_flota
     * usando la sintaxis de RestClient. [cite: 357-359]
     * Este es el reemplazo de nuestro antiguo FlotaFeignClient.
     */
    public CamionDTO getCamionById(Long id) {
        try {
            // Construimos la llamada fluida
            return restClient.get()
                    .uri("/api/v1/flota/camiones/{id}", id) // URL relativa al 'baseUrl'
                    .retrieve() // Ejecuta la petición
                    .body(CamionDTO.class); // Convierte el JSON a nuestro DTO [cite: 359]

        } catch (RestClientResponseException ex) {
            // Si el ms_flota devuelve 404 (No Encontrado), el 'retrieve()' lanza una excepción.
            // La capturamos para manejarla (ej: logging) [cite: 239-242]
            System.err.println("Error al consultar ms_flota: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
            return null; // Devolvemos null para que el 'TramoService' sepa que falló
        }
    }
// ... (El constructor y el método getCamionById() ya están aquí)

    /**
     * Llama al endpoint 'PUT /api/v1/flota/camiones/{id}/ocupar' de ms_flota.
     */
    public void marcarCamionOcupado(Long id) {
        try {
            restClient.put()
                    .uri("/api/v1/flota/camiones/{id}/ocupar", id)
                    .retrieve() // Ejecuta la petición
                    .toBodilessEntity(); // No esperamos un cuerpo de respuesta, solo un 200 OK

        } catch (RestClientResponseException ex) {
            // Manejar el error si ms_flota no encuentra el camión (404)
            System.err.println("Error al marcar camión como ocupado: " + ex.getMessage());
            // (Podríamos querer relanzar una excepción aquí)
        }
    }

    /**
     * Llama al endpoint 'PUT /api/v1/flota/camiones/{id}/liberar' de ms_flota.
     */
    public void marcarCamionDisponible(Long id) {
        try {
            restClient.put()
                    .uri("/api/v1/flota/camiones/{id}/liberar", id)
                    .retrieve()
                    .toBodilessEntity(); // No esperamos respuesta

        } catch (RestClientResponseException ex) {
            System.err.println("Error al marcar camión como disponible: " + ex.getMessage());
        }
    }
    // ... imports necesarios (List, ParameterizedTypeReference)

    public List<CamionDTO> getCamionesAptos(double peso, double volumen) {
        try {
            String url = String.format("/api/v1/flota/camiones/aptos?peso=%s&volumen=%s", peso, volumen);
            return restClient.get().uri(url).retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<java.util.List<CamionDTO>>() {});
        } catch (Exception ex) {
            return java.util.List.of();
        }
    }
}