package com.tpi.logistica.gateway_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para el registro de un nuevo cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroClienteDTO {

    private String email;
    private String password;
    private String nombre;
    private String apellido;
    private String telefono;
}

