package com.tpi.logistica.gateway_service.controller;

import com.tpi.logistica.gateway_service.dto.RegistroClienteDTO;
import com.tpi.logistica.gateway_service.service.KeycloakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para operaciones de autenticación y registro.
 * Sigue la filosofía del proyecto: controlador REST estándar con endpoints simples.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final KeycloakService keycloakService;

    public AuthController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    /**
     * Endpoint para registrar un nuevo cliente en Keycloak.
     * Crea el usuario y le asigna automáticamente el rol CLIENTE.
     * 
     * URL: POST /api/v1/auth/registro
     * Body: {
     *   "email": "cliente@ejemplo.com",
     *   "password": "password123",
     *   "nombre": "Juan",
     *   "apellido": "Pérez",
     *   "telefono": "+5491234567890"
     * }
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrarCliente(@RequestBody RegistroClienteDTO registro) {
        try {
            String userId = keycloakService.registrarCliente(registro);
            return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Cliente registrado exitosamente en Keycloak",
                "userId", userId,
                "email", registro.getEmail()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}

