package com.tpi.logistica.ms_tarifas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tarifas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * El TPI dice "Tarifa: de acuerdo con el diseño de cada grupo".
     * Vamos a crear una tabla simple de clave-valor para parámetros
     * que el Admin pueda modificar.
     */

    // Ej: "COSTO_KM_BASE", "CARGO_GESTION_TRAMO", "PRECIO_COMBUSTIBLE_LITRO"
    @Column(name = "tipo_tarifa", unique = true, nullable = false)
    private String tipoTarifa;

    @Column(nullable = false)
    private Double valor;

    private String descripcion;
}