package com.tpi.logistica.ms_transporte.controller;

import com.tpi.logistica.ms_transporte.model.Ruta;
import com.tpi.logistica.ms_transporte.service.RutaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transporte/rutas")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    /**
     * Endpoint para OBTENER TODAS las rutas.
     */
    @GetMapping
    public ResponseEntity<List<Ruta>> getAllRutas() {
        return ResponseEntity.ok(rutaService.getAllRutas());
    }

    /**
     * Endpoint para CREAR una nueva Ruta y asociarla a una Solicitud.
     * Permite crear múltiples rutas alternativas para una misma solicitud.
     * Resuelve: RF 4
     * URL: POST http://localhost:8080/api/v1/transporte/rutas?solicitudId=1
     */
    @PostMapping
    public ResponseEntity<Ruta> createRuta(@RequestBody Ruta ruta, @RequestParam Long solicitudId) {
        try {
            Ruta nuevaRuta = rutaService.createRuta(ruta, solicitudId);
            return ResponseEntity.status(201).body(nuevaRuta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // (Mejorar manejo de error)
        }
    }

    /**
     * Endpoint para OBTENER todas las rutas alternativas de una solicitud.
     * Permite comparar diferentes opciones de rutas antes de seleccionar una.
     * URL: GET http://localhost:8080/api/v1/transporte/rutas/solicitud/{solicitudId}
     */
    @GetMapping("/solicitud/{solicitudId}")
    public ResponseEntity<List<Ruta>> getRutasPorSolicitud(@PathVariable Long solicitudId) {
        try {
            List<Ruta> rutas = rutaService.getRutasPorSolicitud(solicitudId);
            return ResponseEntity.ok(rutas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para OBTENER la ruta seleccionada de una solicitud.
     * URL: GET http://localhost:8080/api/v1/transporte/rutas/solicitud/{solicitudId}/seleccionada
     */
    @GetMapping("/solicitud/{solicitudId}/seleccionada")
    public ResponseEntity<Ruta> getRutaSeleccionada(@PathVariable Long solicitudId) {
        try {
            Ruta ruta = rutaService.getRutaSeleccionada(solicitudId);
            if (ruta == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ruta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para SELECCIONAR una ruta como la activa para una solicitud.
     * Deselecciona automáticamente cualquier otra ruta que esté seleccionada.
     * URL: PUT http://localhost:8080/api/v1/transporte/rutas/{rutaId}/seleccionar
     */
    @PutMapping("/{rutaId}/seleccionar")
    public ResponseEntity<Ruta> seleccionarRuta(@PathVariable Long rutaId) {
        try {
            Ruta ruta = rutaService.seleccionarRuta(rutaId);
            return ResponseEntity.ok(ruta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}