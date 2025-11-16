package com.tpi.logistica.ms_transporte.repository;

import com.tpi.logistica.ms_transporte.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {

    /**
     * Busca un estado por su descripción exacta (ej: "BORRADOR", "ASIGNADO").
     * Lo usamos en los Services para recuperar el objeto Estado antes de guardar.
     */
    Optional<Estado> findByDescripcion(String descripcion);
}