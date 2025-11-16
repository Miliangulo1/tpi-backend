package com.tpi.logistica.gateway_service.config;

// Imports de Spring Security (WebFlux)
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // ¡Importante!
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

// Imports para el Conversor de Roles (JWT)
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// ¡IMPORTANTE! Necesitás este import que faltaba en el código anterior
import org.springframework.security.config.Customizer;

// ... (imports igual que antes)
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(ex -> ex
                // Flota
                .pathMatchers(HttpMethod.POST, "/api/v1/flota/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/flota/**").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.GET, "/api/v1/flota/camiones/aptos").authenticated() // Nuevo
                .pathMatchers(HttpMethod.GET, "/api/v1/flota/**").hasAnyRole("OPERADOR", "CLIENTE", "TRANSPORTISTA")

                // Transporte
                .pathMatchers(HttpMethod.POST, "/api/v1/transporte/solicitudes").hasRole("CLIENTE") // RF 1
                .pathMatchers(HttpMethod.GET, "/api/v1/transporte/solicitudes/{id}/seguimiento").hasAnyRole("CLIENTE", "OPERADOR") // RF 2
                .pathMatchers(HttpMethod.POST, "/api/v1/transporte/solicitudes/calcular-tentativa").hasRole("OPERADOR") // RF 3
                .pathMatchers(HttpMethod.GET, "/api/v1/transporte/solicitudes/pendientes").hasRole("OPERADOR") // RF 5

                .pathMatchers(HttpMethod.POST, "/api/v1/transporte/rutas").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/transporte/rutas/{rutaId}/seleccionar").hasRole("OPERADOR")
                .pathMatchers(HttpMethod.GET, "/api/v1/transporte/rutas/solicitud/**").hasAnyRole("OPERADOR", "CLIENTE")
                .pathMatchers(HttpMethod.POST, "/api/v1/transporte/tramos").hasRole("OPERADOR")
                // Solo OPERADOR puede asignar camiones
                .pathMatchers(HttpMethod.PUT, "/api/v1/transporte/tramos/{idTramo}/asignar-camion").hasRole("OPERADOR")

                // IMPORTANTE: Estos deben estar ANTES del matcher general de GET
                // Solo TRANSPORTISTA puede iniciar/finalizar tramos
                .pathMatchers(HttpMethod.POST, "/api/v1/transporte/tramos/{idTramo}/iniciar").hasRole("TRANSPORTISTA")
                .pathMatchers(HttpMethod.POST, "/api/v1/transporte/tramos/{idTramo}/finalizar").hasRole("TRANSPORTISTA")

                .pathMatchers(HttpMethod.GET, "/api/v1/transporte/**").hasAnyRole("OPERADOR", "CLIENTE", "TRANSPORTISTA")

                // Tarifas
                .pathMatchers("/api/v1/tarifas/**").hasRole("OPERADOR")

                // Autenticación - registro público (sin autenticación requerida)
                .pathMatchers(HttpMethod.POST, "/api/v1/auth/registro").permitAll()

                .anyExchange().authenticated()
        ).oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }

    /**
     * [cite_start]Este Bean es el "traductor" de roles [cite: 2476-2500].
     */
    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return jwt -> {
            try {
                Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
                if (realmAccess == null || realmAccess.get("roles") == null) {
                    // Si no hay roles en realm_access, intentar buscar en resource_access
                    System.err.println("Warning: No se encontraron roles en realm_access del JWT");
                    return Mono.just(new JwtAuthenticationToken(jwt, List.of()));
                }
                
                List<GrantedAuthority> authorities = realmAccess.get("roles")
                        .stream()
                        .map(roleName -> "ROLE_" + roleName.toUpperCase()) // Convierte a ROLE_TRANSPORTISTA
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                
                // Debug: Imprimir roles extraídos
                System.out.println("Roles extraídos del JWT: " + authorities);
                
                return Mono.just(new JwtAuthenticationToken(jwt, authorities));
            } catch (Exception e) {
                System.err.println("Error al extraer roles del JWT: " + e.getMessage());
                return Mono.just(new JwtAuthenticationToken(jwt, List.of()));
            }
        };
    }
}