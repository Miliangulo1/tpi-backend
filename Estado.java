package com.tpi.logistica.ms_transporte.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "estados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ej: "BORRADOR", "EN_VIAJE", "ENTREGADA"
    @Column(nullable = false, unique = true)
    private String descripcion;
}