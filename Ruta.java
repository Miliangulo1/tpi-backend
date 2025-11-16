package com.tpi.logistica.ms_transporte.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "rutas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * --- RELACIÓN CON SOLICITUD (CAMBIADA A MANY-TO-ONE) ---
     * @ManyToOne: "Muchas rutas pueden pertenecer a Una Solicitud".
     * Permite tener múltiples rutas alternativas para una misma solicitud.
     */
    @ManyToOne
    @JoinColumn(name = "solicitud_id", nullable = false)
    @JsonIgnore // Ignorar completamente la solicitud para evitar bucles
    private Solicitud solicitud;

    @Column(name = "cantidad_tramos")
    private int cantidadTramos;

    @Column(name = "cantidad_depositos")
    private int cantidadDepositos;

    /**
     * Campo para marcar si esta ruta fue seleccionada para ejecutarse.
     * Solo una ruta por solicitud debe estar seleccionada.
     */
    @Column(name = "seleccionada", nullable = false)
    private boolean seleccionada = false;

    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("ruta")
    private List<Tramo> tramos;
}