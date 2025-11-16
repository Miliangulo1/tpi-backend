package com.tpi.logistica.ms_tarifas.controller;

import com.tpi.logistica.ms_tarifas.model.Tarifa;
import com.tpi.logistica.ms_tarifas.service.TarifaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// ¡IMPORTANTE! La URL base debe coincidir con la del Gateway
@RequestMapping("/api/v1/tarifas")
public class TarifaController {

    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    /**
     * Endpoint para OBTENER TODAS las tarifas.
     * URL: GET http://localhost:8080/api/v1/tarifas
     */
    @GetMapping
    public ResponseEntity<List<Tarifa>> getAllTarifas() {
        List<Tarifa> tarifas = tarifaService.getAllTarifas();
        return ResponseEntity.ok(tarifas);
    }

    /**
     * Endpoint para CREAR una nueva tarifa.
     * URL: POST http://localhost:8080/api/v1/tarifas
     */
    @PostMapping
    public ResponseEntity<Tarifa> createTarifa(@RequestBody Tarifa tarifa) {
        Tarifa nuevaTarifa = tarifaService.createOrUpdateTarifa(tarifa);
        return ResponseEntity.status(201).body(nuevaTarifa);
    }
    // ... (El constructor y los endpoints (GET y POST) sin ID ya están aquí)

    /**
     * Endpoint para OBTENER una tarifa por ID.
     * Resuelve: RF 10
     * URL: GET http://localhost:8080/api/v1/tarifas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> getTarifaById(@PathVariable Long id) {
        try {
            Tarifa tarifa = tarifaService.getTarifaById(id);
            return ResponseEntity.ok(tarifa);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para ACTUALIZAR una tarifa por ID.
     * Resuelve: RF 10 (Actualizar tarifas)
     * URL: PUT http://localhost:8080/api/v1/tarifas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tarifa> updateTarifa(@PathVariable Long id, @RequestBody Tarifa tarifaDetails) {
        try {
            Tarifa tarifaActualizada = tarifaService.updateTarifa(id, tarifaDetails);
            return ResponseEntity.ok(tarifaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para BORRAR una tarifa por ID.
     * Resuelve: RF 10
     * URL: DELETE http://localhost:8080/api/v1/tarifas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarifa(@PathVariable Long id) {
        try {
            tarifaService.deleteTarifa(id);
            return ResponseEntity.noContent().build(); // Devuelve 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}