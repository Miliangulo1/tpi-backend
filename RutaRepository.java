package com.tpi.logistica.ms_transporte.repository;

import com.tpi.logistica.ms_transporte.model.Ruta;
import com.tpi.logistica.ms_transporte.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
    
    /**
     * Busca todas las rutas de una solicitud.
     */
    List<Ruta> findBySolicitud(Solicitud solicitud);
    
    /**
     * Busca todas las rutas de una solicitud por su ID.
     */
    List<Ruta> findBySolicitud_Id(Long solicitudId);
    
    /**
     * Busca la ruta seleccionada de una solicitud.
     */
    Optional<Ruta> findBySolicitudAndSeleccionadaTrue(Solicitud solicitud);
}