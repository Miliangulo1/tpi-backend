package com.tpi.logistica.ms_flota.repository;

import com.tpi.logistica.ms_flota.model.Deposito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Le dice a Spring que esto es un Repositorio
public interface DepositoRepository extends JpaRepository<Deposito, Long> {

    // Al igual que con Camion, Spring Data JPA nos da
    // automáticamente los métodos:
    // save(), findById(), findAll(), deleteById(), etc.

}