package com.tpi.logistica.ms_transporte.service;

import com.tpi.logistica.ms_transporte.model.Estado;
import com.tpi.logistica.ms_transporte.model.HistorialEstado;
import com.tpi.logistica.ms_transporte.model.Solicitud;
import com.tpi.logistica.ms_transporte.repository.HistorialEstadoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestionar el historial de cambios de estado.
 */
@Service
public class HistorialEstadoService {

    private final HistorialEstadoRepository historialEstadoRepository;

    public HistorialEstadoService(HistorialEstadoRepository historialEstadoRepository) {
        this.historialEstadoRepository = historialEstadoRepository;
    }

    /**
     * Registra un cambio de estado en el historial de una solicitud.
     * 
     * @param solicitud La solicitud cuyo estado cambió
     * @param estado El nuevo estado
     * @param observaciones Observaciones opcionales sobre el cambio
     * @return El registro del historial creado
     */
    public HistorialEstado registrarCambioEstado(Solicitud solicitud, Estado estado, String observaciones) {
        HistorialEstado historial = new HistorialEstado();
        historial.setSolicitud(solicitud);
        historial.setEstado(estado);
        historial.setFechaHoraCambio(LocalDateTime.now());
        historial.setObservaciones(observaciones);
        
        return historialEstadoRepository.save(historial);
    }

    /**
     * Obtiene el historial completo de cambios de estado de una solicitud
     * ordenado cronológicamente (más antiguo primero).
     * 
     * @param solicitudId El ID de la solicitud
     * @return Lista de cambios de estado ordenados por fecha
     */
    public List<HistorialEstado> getHistorialPorSolicitud(Long solicitudId) {
        return historialEstadoRepository.findBySolicitud_IdOrderByFechaHoraCambioAsc(solicitudId);
    }

    /**
     * Obtiene el historial completo de cambios de estado de una solicitud.
     * 
     * @param solicitud La solicitud
     * @return Lista de cambios de estado ordenados por fecha
     */
    public List<HistorialEstado> getHistorialPorSolicitud(Solicitud solicitud) {
        return historialEstadoRepository.findBySolicitud(solicitud);
    }
}

