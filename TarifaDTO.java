package com.tpi.logistica.ms_transporte.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaDTO {

    private Long id;
    private String tipoTarifa; // Ej: "PRECIO_COMBUSTIBLE_LITRO"
    private Double valor;
    private String descripcion;

}