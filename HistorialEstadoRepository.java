package com.tpi.logistica.ms_transporte.repository;

import com.tpi.logistica.ms_transporte.model.HistorialEstado;
import com.tpi.logistica.ms_transporte.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {

    /**
     * Busca todos los cambios de estado de una solicitud ordenados cronológicamente.
     * Spring Data JPA interpreta el guion bajo "_" como un separador de navegación:
     * Entidad HistorialEstado -> campo 'solicitud' -> campo 'id'.
     */
    List<HistorialEstado> findBySolicitud_IdOrderByFechaHoraCambioAsc(Long solicitudId);

    /**
     * Busca todos los cambios de estado de una solicitud.
     */
    List<HistorialEstado> findBySolicitud(Solicitud solicitud);
}

