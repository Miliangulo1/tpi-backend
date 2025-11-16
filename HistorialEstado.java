package com.tpi.logistica.ms_transporte.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // ¡¡ASEGURATE DE QUE ESTE IMPORT ESTÉ!!
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * --- RELACIÓN CON SOLICITUD ---
     */
    @ManyToOne
    @JoinColumn(name = "solicitud_id", nullable = false)

    // --- ¡¡ESTA ES LA LÍNEA CLAVE QUE ROMPE EL BUCLE!! ---
    @JsonIgnore
    private Solicitud solicitud;

    /**
     * --- RELACIÓN CON ESTADO ---
     */
    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estado;

    @Column(name = "fecha_hora_cambio", nullable = false)
    private LocalDateTime fechaHoraCambio;

    @Column(name = "observaciones", length = 500)
    private String observaciones;
}