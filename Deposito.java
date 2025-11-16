package com.tpi.logistica.ms_flota.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity // Le dice a JPA: "Esta clase es una tabla"
@Table(name = "depositos") // El nombre que tendrá la tabla en PostgreSQL
@Data // De Lombok: crea getters, setters, toString(), etc.
@NoArgsConstructor // De Lombok: crea un constructor vacío
@AllArgsConstructor // De Lombok: crea un constructor con todos los campos
public class Deposito {

    @Id // Marca este campo como la Primary Key (PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // El TPI lo llama "identificación"

    @Column(nullable = false) // Hacemos que el nombre sea obligatorio
    private String nombre;

    private String direccion;

    // El TPI pide "coordenadas", las separamos en latitud y longitud
    @Column(nullable = false)
    private double latitud;

    @Column(nullable = false)
    private double longitud;

    // El TPI pide un costo de estadía en el Requerimiento 8.3 y Reglas de Negocio
    @Column(name = "costo_estadia_diario")
    private double costoEstadiaDiario;
}