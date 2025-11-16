package com.tpi.logistica.ms_transporte.repository;

import com.tpi.logistica.ms_transporte.model.Tramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {
}