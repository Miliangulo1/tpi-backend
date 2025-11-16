package com.tpi.logistica.ms_transporte.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // El TPI lo llama "número"

    /**
     * --- RELACIÓN CON CONTENEDOR ---
     * @ManyToOne: "Muchas solicitudes pueden estar asociadas a Un contenedor".
     * @JoinColumn: Crea una columna "contenedor_id" en esta tabla.
     */
    @ManyToOne
    @JoinColumn(name = "contenedor_id", nullable = false)
    private Contenedor contenedor;

    /**
     * --- RELACIÓN CON CLIENTE ---
     * @ManyToOne: "Muchas solicitudes pueden ser de Un cliente".
     * @JoinColumn: Crea una columna "cliente_id" en esta tabla.
     */
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // --- Campos de Origen y Destino (del TPI) ---
    @Column(name = "origen_direccion", nullable = false)
    private String origenDireccion;

    @Column(name = "origen_latitud", nullable = false)
    private double origenLatitud;

    @Column(name = "origen_longitud", nullable = false)
    private double origenLongitud;

    @Column(name = "destino_direccion", nullable = false)
    private String destinoDireccion;

    @Column(name = "destino_latitud", nullable = false)
    private double destinoLatitud;

    @Column(name = "destino_longitud", nullable = false)
    private double destinoLongitud;

    // --- Campos de Costos y Tiempos (del TPI) ---
    @Column(name = "costo_estimado")
    private Double costoEstimado; // Usamos Double (objeto) para que pueda ser nulo al inicio

    @Column(name = "tiempo_estimado_hs")
    private Double tiempoEstimado;

    @Column(name = "costo_final")
    private Double costoFinal;

    @Column(name = "tiempo_real_hs")
    private Double tiempoReal;

    // --- Estado (del TPI) ---
    // Ej: [borrador, programada, en tránsito, entregada]
    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estado;


    /**
     * --- RELACIÓN CON RUTAS (CAMBIADA A ONE-TO-MANY) ---
     * @OneToMany: "Una Solicitud puede tener Muchas Rutas alternativas".
     * Permite tener múltiples rutas alternativas y seleccionar una.
     * @JoinColumn: La columna "solicitud_id" está en la tabla "rutas".
     * NOTA: Se ignora en JSON por defecto para evitar bucles. Se carga explícitamente cuando se necesita.
     */
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Ignorar rutas por defecto en JSON para evitar bucles
    private List<Ruta> rutas;
}