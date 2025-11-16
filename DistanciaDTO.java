package com.tpi.logistica.ms_transporte.dto;

import lombok.Data;

@Data
public class DistanciaDTO {
    private String origen;
    private String destino;
    private double kilometros;
    private String duracionTexto;
    private double duracionSegundos;
}