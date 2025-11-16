package com.tpi.logistica.ms_flota.repository;

import com.tpi.logistica.ms_flota.model.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> {

    // Nuevo método para RF 3
    List<Camion> findByDisponibleTrueAndCapacidadPesoGreaterThanEqualAndCapacidadVolumenGreaterThanEqual(double peso, double volumen);
}