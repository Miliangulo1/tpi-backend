package com.tpi.logistica.ms_flota.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity // Le dice a JPA: "Esta clase es una tabla"
@Table(name = "camiones") // El nombre que tendrá la tabla en PostgreSQL
@Data // De Lombok: crea getters, setters, toString(), etc.
@NoArgsConstructor // De Lombok: crea un constructor vacío
@AllArgsConstructor // De Lombok: crea un constructor con todos los campos
public class Camion {

    @Id // Marca este campo como la Primary Key (PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Le dice a Postgres que este valor es autoincremental
    private Long id;

    // El TPI lo llama "dominio". Lo hacemos único y no nulo.
    @Column(name = "patente", unique = true, nullable = false)
    private String patente;

    @Column(name = "nombre_transportista")
    private String nombreTransportista;

    private String telefono; // No necesita @Column si se llama igual

    @Column(name = "capacidad_peso_kg") // Es buena práctica incluir unidades
    private double capacidadPeso;

    @Column(name = "capacidad_volumen_m3") // Es buena práctica incluir unidades
    private double capacidadVolumen;

    @Column(name = "costo_por_km")
    private double costoPorKm;

    @Column(name = "consumo_combustible_promedio_km_l")
    private double consumoCombustiblePromedio;

    // El TPI lo llama "disponibilidad"
    private boolean disponible;
}