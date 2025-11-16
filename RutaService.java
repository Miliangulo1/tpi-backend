package com.tpi.logistica.ms_transporte.service;

import com.tpi.logistica.ms_transporte.model.Estado;
import com.tpi.logistica.ms_transporte.model.Ruta;
import com.tpi.logistica.ms_transporte.model.Solicitud;
import com.tpi.logistica.ms_transporte.repository.EstadoRepository;
import com.tpi.logistica.ms_transporte.repository.RutaRepository;
import com.tpi.logistica.ms_transporte.repository.SolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ¡Importante!

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final SolicitudRepository solicitudRepository;
    private final EstadoRepository estadoRepository;
    private final HistorialEstadoService historialEstadoService;

    public RutaService(RutaRepository rutaRepository,
                       SolicitudRepository solicitudRepository,
                       EstadoRepository estadoRepository,
                       HistorialEstadoService historialEstadoService) {
        this.rutaRepository = rutaRepository;
        this.solicitudRepository = solicitudRepository;
        this.estadoRepository = estadoRepository;
        this.historialEstadoService = historialEstadoService;
    }

    // --- MÉTODO AUXILIAR ---
    private Estado buscarEstado(String descripcion) {
        return estadoRepository.findByDescripcion(descripcion)
                .orElseThrow(() -> new RuntimeException("Estado '" + descripcion + "' no encontrado."));
    }

    /**
     * Resuelve RF 4: Crea una ruta alternativa para una solicitud.
     */
    public Ruta createRuta(Ruta ruta, Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada con id: " + solicitudId));

        ruta.setSolicitud(solicitud);
        ruta.setSeleccionada(false); // Por defecto no está seleccionada

        return rutaRepository.save(ruta);
    }

    public List<Ruta> getAllRutas() {
        return rutaRepository.findAll();
    }

    /**
     * Obtiene todas las rutas alternativas de una solicitud (RF 3).
     */
    public List<Ruta> getRutasPorSolicitud(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada con id: " + solicitudId));
        return rutaRepository.findBySolicitud(solicitud);
    }

    /**
     * Obtiene la ruta seleccionada de una solicitud.
     */
    public Ruta getRutaSeleccionada(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada con id: " + solicitudId));
        return rutaRepository.findBySolicitudAndSeleccionadaTrue(solicitud)
                .orElse(null); // Es válido que no haya ninguna seleccionada
    }

    /**
     * Selecciona una ruta como la activa para una solicitud (RF 4).
     */
    @Transactional // Asegura que la operación (múltiples updates) sea atómica
    public Ruta seleccionarRuta(Long rutaId) {
        Ruta rutaASeleccionar = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new NoSuchElementException("Ruta no encontrada con id: " + rutaId));

        Solicitud solicitud = rutaASeleccionar.getSolicitud();

        // 1. Deseleccionar todas las otras rutas de esta solicitud
        List<Ruta> todasLasRutas = rutaRepository.findBySolicitud(solicitud);
        for (Ruta r : todasLasRutas) {
            r.setSeleccionada(false);
        }

        // 2. Seleccionar la ruta especificada
        rutaASeleccionar.setSeleccionada(true);
        rutaRepository.saveAll(todasLasRutas); // Guarda todos los cambios

        // 3. Actualizar estado de la Solicitud
        Estado estadoPendiente = buscarEstado("PENDIENTE");
        solicitud.setEstado(estadoPendiente);
        historialEstadoService.registrarCambioEstado(solicitud, estadoPendiente, "Ruta (ID: " + rutaASeleccionar.getId() + ") seleccionada por Operador.");
        solicitudRepository.save(solicitud);

        return rutaASeleccionar;
    }
}