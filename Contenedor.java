package com.tpi.logistica.ms_transporte.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "contenedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // El TPI lo llama "identificación"

    @Column(name = "peso_kg")
    private double peso;

    @Column(name = "volumen_m3")
    private double volumen;

    // El TPI pide "estado", pero el estado es de la *solicitud*, no del contenedor físico.
    // Lo omitimos aquí y lo ponemos en la Solicitud.

    /**
     * --- ¡NUESTRA PRIMERA RELACIÓN! ---
     * El TPI dice "cliente asociado".
     * @ManyToOne: "Muchos contenedores pueden pertenecer a Un cliente".
     * @JoinColumn: Crea una columna "cliente_id" en esta tabla (contenedores)
     * que será la Foreign Key (FK) a la tabla "clientes".
     */
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties("contenedores") // Evita bucles si Cliente tiene relación con Contenedor
    private Cliente cliente;
}