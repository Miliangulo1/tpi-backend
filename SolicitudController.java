package com.tpi.logistica.ms_transporte.controller;

import com.tpi.logistica.ms_transporte.model.Solicitud;
import com.tpi.logistica.ms_transporte.service.SolicitudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/transporte/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // --- ENDPOINTS CRUD BÁSICOS ---

    @GetMapping
    public ResponseEntity<List<Solicitud>> getAllSolicitudes() {
        return ResponseEntity.ok(solicitudService.getAllSolicitudes());
    }

    @PostMapping
    public ResponseEntity<?> createSolicitud(@RequestBody Solicitud solicitud) {
        try {
            Solicitud nuevaSolicitud = solicitudService.createSolicitud(solicitud);
            return ResponseEntity.status(201).body(nuevaSolicitud);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSolicitudById(@PathVariable Long id) {
        try {
            Solicitud solicitud = solicitudService.getSolicitudById(id);
            return ResponseEntity.ok(solicitud);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Solicitud> updateSolicitud(@PathVariable Long id, @RequestBody Solicitud solicitudDetails) {
        return ResponseEntity.ok(solicitudService.updateSolicitud(id, solicitudDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolicitud(@PathVariable Long id) {
        solicitudService.deleteSolicitud(id);
        return ResponseEntity.noContent().build();
    }

    // --- ENDPOINTS DE LÓGICA DE NEGOCIO DEL TPI ---

    /**
     * Resuelve RF 2: Seguimiento con historial cronológico.
     * Retorna la solicitud y su historial de estados ordenado cronológicamente.
     */
    @GetMapping("/{id}/seguimiento")
    public ResponseEntity<?> getSeguimiento(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(solicitudService.getSeguimiento(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    /**
     * Resuelve RF 3: Ruta Tentativa.
     */
    @PostMapping("/calcular-tentativa")
    public ResponseEntity<?> calcularRutaTentativa(@RequestBody Solicitud solicitud) {
        try {
            Solicitud solicitudCalculada = solicitudService.calcularRutaTentativa(solicitud);
            return ResponseEntity.ok(solicitudCalculada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Resuelve RF 5: Consultar Pendientes.
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<Solicitud>> getSolicitudesPendientes() {
        return ResponseEntity.ok(solicitudService.getSolicitudesPendientes());
    }
}