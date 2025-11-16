package com.tpi.logistica.ms_transporte.repository;

import com.tpi.logistica.ms_transporte.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    /**
     * Busca solicitudes cuyo estado tenga una descripción que NO esté en la lista.
     * Spring Data JPA interpreta el guion bajo "_" como un separador de navegación:
     * Entidad Solicitud -> campo 'estado' -> campo 'descripcion'.
     * * Resuelve: RF 5 ("Consultar todos los contenedores pendientes de entrega")
     */
    List<Solicitud> findByEstado_DescripcionNotIn(List<String> descripciones);
}