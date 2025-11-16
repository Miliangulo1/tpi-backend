package com.tpi.logistica.ms_transporte.repository;

import com.tpi.logistica.ms_transporte.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // ¡Importante!

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email); // ¡Este es el que falta!
}