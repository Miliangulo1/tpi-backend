package com.tpi.logistica.ms_flota.controller;

// Imports
import com.tpi.logistica.ms_flota.model.Camion;
import com.tpi.logistica.ms_flota.model.Deposito; // <- NUEVO IMPORT
import com.tpi.logistica.ms_flota.service.CamionService;
import com.tpi.logistica.ms_flota.service.DepositoService; // <- NUEVO IMPORT
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flota")
public class FlotaController {

    // --- 1. AHORA INYECTAMOS AMBOS SERVICIOS ---
    private final CamionService camionService;
    private final DepositoService depositoService; // <- NUEVA INYECCIÓN

    // Actualizamos el constructor para recibir los dos servicios
    public FlotaController(CamionService camionService, DepositoService depositoService) {
        this.camionService = camionService;
        this.depositoService = depositoService;
    }

    // --- Endpoints de CAMIONES (Sin cambios) ---

    @GetMapping("/camiones")
    public ResponseEntity<List<Camion>> getAllCamiones() {
        List<Camion> camiones = camionService.getAllCamiones();
        return ResponseEntity.ok(camiones);
    }

    @PostMapping("/camiones")
    public ResponseEntity<Camion> createCamion(@RequestBody Camion camion) {
        Camion nuevoCamion = camionService.createCamion(camion);
        return ResponseEntity.status(201).body(nuevoCamion);
    }

    @GetMapping("/camiones/{id}")
    public ResponseEntity<Camion> getCamionById(@PathVariable Long id) {
        try {
            Camion camion = camionService.getCamionById(id);
            return ResponseEntity.ok(camion);
        } catch (RuntimeException e) {
            // Devuelve un 404 Not Found si el servicio lanza la excepción
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/camiones/{id}")
    public ResponseEntity<Camion> updateCamion(@PathVariable Long id, @RequestBody Camion camionDetails) {
        try {
            Camion camionActualizado = camionService.updateCamion(id, camionDetails);
            return ResponseEntity.ok(camionActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/camiones/{id}")
    public ResponseEntity<Void> deleteCamion(@PathVariable Long id) {
        try {
            camionService.deleteCamion(id);
            return ResponseEntity.noContent().build(); // Devuelve 204 No Content (Éxito sin cuerpo)
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // ... (El constructor y los otros endpoints de /camiones están aquí)

    /**
     * Endpoint para marcar un camión como OCUPADO (no disponible).
     * Resuelve: Lógica de negocio de RF 6 (será llamado por ms_transporte).
     * URL: PUT http://localhost:8080/api/v1/flota/camiones/{id}/ocupar
     */
    @PutMapping("/camiones/{id}/ocupar")
    public ResponseEntity<Camion> marcarCamionOcupado(@PathVariable Long id) {
        try {
            Camion camionActualizado = camionService.marcarCamionOcupado(id);
            return ResponseEntity.ok(camionActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ... (El constructor y los otros endpoints de /camiones están aquí)

    /**
     * Endpoint para marcar un camión como DISPONIBLE (libre).
     * Resuelve: Lógica de negocio de RF 7 (será llamado por ms_transporte).
     * URL: PUT http://localhost:8080/api/v1/flota/camiones/{id}/liberar
     */
    @PutMapping("/camiones/{id}/liberar")
    public ResponseEntity<Camion> marcarCamionDisponible(@PathVariable Long id) {
        try {
            Camion camionActualizado = camionService.marcarCamionDisponible(id);
            return ResponseEntity.ok(camionActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/depositos")
    public ResponseEntity<List<Deposito>> getAllDepositos() {
        List<Deposito> depositos = depositoService.getAllDepositos();
        return ResponseEntity.ok(depositos);
    }


    @PostMapping("/depositos")
    public ResponseEntity<Deposito> createDeposito(@RequestBody Deposito deposito) {
        Deposito nuevoDeposito = depositoService.createDeposito(deposito);
        return ResponseEntity.status(201).body(nuevoDeposito);
    }

    // ... (El constructor y todos los endpoints de /camiones ya están aquí)
    // ... (Los endpoints /depositos (GET y POST) también están aquí)

    /**
     * Endpoint para OBTENER un depósito por ID.
     * Resuelve: RF 10 (Actualizar)
     * URL: GET http://localhost:8080/api/v1/flota/depositos/{id}
     */
    @GetMapping("/depositos/{id}")
    public ResponseEntity<Deposito> getDepositoById(@PathVariable Long id) {
        try {
            Deposito deposito = depositoService.getDepositoById(id);
            return ResponseEntity.ok(deposito);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/depositos/{id}")
    public ResponseEntity<Deposito> updateDeposito(@PathVariable Long id, @RequestBody Deposito depositoDetails) {
        try {
            Deposito depositoActualizado = depositoService.updateDeposito(id, depositoDetails);
            return ResponseEntity.ok(depositoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/depositos/{id}")
    public ResponseEntity<Void> deleteDeposito(@PathVariable Long id) {
        try {
            depositoService.deleteDeposito(id);
            return ResponseEntity.noContent().build(); // Devuelve 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ... dentro de FlotaController
    @GetMapping("/camiones/aptos")
    public ResponseEntity<List<Camion>> getCamionesAptos(@RequestParam double peso, @RequestParam double volumen) {
        List<Camion> camiones = camionService.getCamionesAptos(peso, volumen);
        if (camiones.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(camiones);
    }

}