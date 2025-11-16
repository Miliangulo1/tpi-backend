package com.tpi.logistica.ms_transporte.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Objeto simple para transferir datos a través de la red
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CamionDTO {

    private Long id;
    private String patente;
    private double capacidadPeso;
    private double capacidadVolumen;
    private double costoPorKm;
    private double consumoCombustiblePromedio;
    private boolean disponible;

    // Solo incluimos las propiedades que ms_transporte necesita consultar
}