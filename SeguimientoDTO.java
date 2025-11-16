package com.tpi.logistica.ms_transporte.dto;

import com.tpi.logistica.ms_transporte.model.HistorialEstado;
import com.tpi.logistica.ms_transporte.model.Solicitud;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO para el seguimiento de una solicitud.
 * Incluye la información de la solicitud y su historial cronológico de estados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoDTO {

    /**
     * Información básica de la solicitud.
     */
    private Solicitud solicitud;

    /**
     * Historial cronológico de cambios de estado.
     * Ordenado desde el más antiguo al más reciente.
     */
    private List<HistorialEstado> historialEstados;
}

