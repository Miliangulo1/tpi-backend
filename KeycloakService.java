package com.tpi.logistica.gateway_service.service;

import com.tpi.logistica.gateway_service.dto.RegistroClienteDTO;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Servicio para gestionar usuarios en Keycloak.
 * Sigue la filosofía del proyecto: servicio con lógica de negocio.
 */
@Service
public class KeycloakService {

    @Value("${keycloak.server-url:http://localhost:8180}")
    private String serverUrl;

    @Value("${keycloak.realm:tpi-logistica}")
    private String realm;

    @Value("${keycloak.client-id:admin-cli}")
    private String clientId;

    @Value("${keycloak.admin-username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin-password:admin}")
    private String adminPassword;

    /**
     * Obtiene una instancia de Keycloak Admin Client autenticada.
     * Usa credenciales de administrador para realizar operaciones.
     */
    private Keycloak getKeycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .username(adminUsername)
                .password(adminPassword)
                .clientId(clientId)
                .build();
    }

    /**
     * Registra un nuevo cliente en Keycloak.
     * Crea el usuario y le asigna automáticamente el rol CLIENTE.
     * 
     * @param registro Datos del cliente a registrar
     * @return ID del usuario creado en Keycloak
     * @throws RuntimeException si el usuario ya existe o hay un error al crearlo
     */
    public String registrarCliente(RegistroClienteDTO registro) {
        Keycloak keycloak = getKeycloakAdminClient();

        // Verificar si el usuario ya existe
        List<UserRepresentation> usuariosExistentes = keycloak.realm(realm)
                .users()
                .search(registro.getEmail(), true);
        
        if (!usuariosExistentes.isEmpty()) {
            throw new RuntimeException("El usuario con email '" + registro.getEmail() + "' ya existe en Keycloak.");
        }

        // Crear representación del usuario
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmail(registro.getEmail());
        user.setUsername(registro.getEmail()); // Usar email como username
        user.setFirstName(registro.getNombre());
        user.setLastName(registro.getApellido());
        user.setEmailVerified(false); // Requerir verificación de email si es necesario

        // Crear credenciales
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(registro.getPassword());
        credential.setTemporary(false); // La contraseña no es temporal
        user.setCredentials(Collections.singletonList(credential));

        // Crear el usuario en Keycloak
        Response response = keycloak.realm(realm).users().create(user);
        
        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
            String errorMessage = response.readEntity(String.class);
            response.close();
            throw new RuntimeException("Error al crear usuario en Keycloak: " + errorMessage);
        }

        // Obtener el ID del usuario creado
        String location = response.getLocation().getPath();
        String userId = location.substring(location.lastIndexOf('/') + 1);
        response.close();

        // Asignar rol CLIENTE al usuario
        try {
            var realmResource = keycloak.realm(realm);
            var roleResource = realmResource.roles().get("CLIENTE");
            if (roleResource == null) {
                throw new RuntimeException("El rol 'CLIENTE' no existe en Keycloak. Debe crearse primero en el realm.");
            }
            
            var userResource = realmResource.users().get(userId);
            userResource.roles().realmLevel().add(Collections.singletonList(roleResource.toRepresentation()));
        } catch (Exception e) {
            // Si falla la asignación de rol, intentar eliminar el usuario creado
            keycloak.realm(realm).users().get(userId).remove();
            throw new RuntimeException("Error al asignar rol CLIENTE: " + e.getMessage());
        }

        keycloak.close();
        return userId;
    }
}

