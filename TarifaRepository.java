package com.tpi.logistica.ms_tarifas.repository;

import com.tpi.logistica.ms_tarifas.model.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    // Spring Data JPA nos da:
    // save(), findById(), findAll(), deleteById(), etc.

    // Podríamos agregar búsquedas personalizadas si quisiéramos,
    // como por ejemplo:
    // Optional<Tarifa> findByTipoTarifa(String tipoTarifa);
}