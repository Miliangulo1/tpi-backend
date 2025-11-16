package com.tpi.logistica.ms_transporte.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; // Usamos LocalDateTime para fechas y horas

@Entity
@Table(name = "tramos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * --- RELACIÓN CON RUTA ---
     * @ManyToOne: "Muchos Tramos pueden pertenecer a Una Ruta".
     * @JoinColumn: Crea una columna "ruta_id" en esta tabla.
     */
// ... dentro de la clase Tramo
    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    @JsonIgnoreProperties("tramos") // Evita el bucle infinito
    @JsonIgnore
    private Ruta ruta;

    // --- RELACIÓN CON CAMION (Tabla de ms_flota) ---
    // Como la tabla 'camiones' vive en la misma BD, podemos mapearla.
    // Pero solo guardamos el ID, ya que la lógica de negocio
    // la maneja ms_flota.
    @Column(name = "camion_id") // Guardamos solo el ID
    private Long camionId; // Puede ser nulo hasta que se asigne

    private String origen; // Ej: "Origen", "Deposito_1"
    private String destino; // Ej: "Deposito_1", "Destino"

    // Ej: [origen-destino, origen-deposito, etc.]
    private String tipo;

    // Ej: [estimado, asignado, iniciado, finalizado]
    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estado;

    @Column(name = "costo_aproximado")
    private Double costoAproximado;

    @Column(name = "costo_real")
    private Double costoReal;

    // --- Fechas (del TPI) ---
    @Column(name = "fecha_hora_inicio_estimada")
    private LocalDateTime fechaHoraInicioEstimada;

    @Column(name = "fecha_hora_fin_estimada")
    private LocalDateTime fechaHoraFinEstimada;

    @Column(name = "fecha_hora_inicio_real")
    private LocalDateTime fechaHoraInicioReal;

    @Column(name = "fecha_hora_fin_real")
    private LocalDateTime fechaHoraFinReal;
}