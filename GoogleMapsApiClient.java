package com.tpi.logistica.ms_transporte.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpi.logistica.ms_transporte.dto.DistanciaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GoogleMapsApiClient {

    @Value("${google.maps.apikey}") // [cite: 181]
    private String apiKey;

    // Inyectamos el Builder, como pide el PDF [cite: 183]
    private final RestClient.Builder restClientBuilder;

    // Inyectamos el ObjectMapper para parsear el JSON
    private final ObjectMapper objectMapper;

    public GoogleMapsApiClient(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.restClientBuilder = restClientBuilder;
        this.objectMapper = objectMapper;
    }

    /**
     * Llama a la API de Google Maps Distance Matrix
     * y parsea manualmente la respuesta, como en el PDF [cite: 184-204].
     */
    public DistanciaDTO calcularDistancia(String origen, String destino) throws Exception {

        // 1. Construimos el RestClient (como en el PDF) [cite: 185-186]
        RestClient client = restClientBuilder
                .baseUrl("https://maps.googleapis.com/maps/api")
                .build();

        // 2. Construimos la URL (como en el PDF) [cite: 187-188]
        // (Formato de coordenadas: "latitud,longitud")
        String url = "/distancematrix/json?origins=" + origen +
                "&destinations=" + destino +
                "&units=metric&key=" + apiKey;

        // 3. Obtenemos la respuesta como String (como en el PDF) [cite: 189-190]
        ResponseEntity<String> response = client.get()
                .uri(url)
                .retrieve()
                .toEntity(String.class);

        // 4. Parseamos el JSON manualmente (como en el PDF) [cite: 191-204]
        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode leg = root.path("rows").get(0).path("elements").get(0);

        // 5. Creamos nuestro DTO de respuesta
        DistanciaDTO dto = new DistanciaDTO();
        dto.setOrigen(origen);
        dto.setDestino(destino);
        dto.setKilometros(leg.path("distance").path("value").asDouble() / 1000.0); // Convertir metros a KM [cite: 200]
        dto.setDuracionTexto(leg.path("duration").path("text").asText()); // [cite: 204]
        dto.setDuracionSegundos(leg.path("duration").path("value").asDouble());

        return dto;
    }
}