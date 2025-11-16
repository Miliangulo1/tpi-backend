package com.tpi.logistica.ms_tarifas;

// Imports originales
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// --- Import NUEVO que agregamos (si no está ya) ---
import java.util.TimeZone;
// ------------------------------------------------

@SpringBootApplication
public class MsTarifasApplication {

    // (¡Borramos el método @PostConstruct de aquí!)

    // --- Método main (original) ---
    public static void main(String[] args) {

        // --- ¡¡LÍNEA CLAVE AGREGADA AQUÍ!! ---
        // Forzamos la zona horaria a UTC ANTES de que Spring arranque.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        // -------------------------------------

        SpringApplication.run(MsTarifasApplication.class, args);
    }

}
