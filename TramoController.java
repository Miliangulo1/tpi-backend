package com.tpi.logistica.ms_transporte.controller;

import com.tpi.logistica.ms_transporte.model.Tramo;
import com.tpi.logistica.ms_transporte.service.TramoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transporte/tramos")
public class TramoController {

    private final TramoService tramoService;

    public TramoController(TramoService tramoService) {
        this.tramoService = tramoService;
    }

    /**
     * Endpoint para ASIGNAR un camión a un tramo.
     * Resuelve: RF 6
     * URL: PUT http://localhost:8080/api/v1/transporte/tramos/{idTramo}/asignar-camion
     */
    @PutMapping("/{idTramo}/asignar-camion")
    public ResponseEntity<?> asignarCamion(@PathVariable Long idTramo,
                                           @RequestParam Long idCamion) {
        try {
            // Aquí es donde se ejecuta la llamada RestClient a ms_flota
            Tramo tramoActualizado = tramoService.asignarCamion(idTramo, idCamion);
            return ResponseEntity.ok(tramoActualizado);
        } catch (Exception e) {
            // Si el camión no existe (404) o no está disponible (IllegalStateException)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint para que un Transportista INICIE un tramo.
     * Resuelve: RF 7
     * URL: POST http://localhost:8080/api/v1/transporte/tramos/{idTramo}/iniciar
     */
    @PostMapping("/{idTramo}/iniciar")
    public ResponseEntity<?> iniciarTramo(@PathVariable Long idTramo) {
        try {
            Tramo tramoActualizado = tramoService.registrarInicioTramo(idTramo);
            return ResponseEntity.ok(tramoActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint para que un Transportista FINALICE un tramo.
     * Resuelve: RF 7
     * URL: POST http://localhost:8080/api/v1/transporte/tramos/{idTramo}/finalizar
     */
    @PostMapping("/{idTramo}/finalizar")
    public ResponseEntity<?> finalizarTramo(@PathVariable Long idTramo) {
        try {
            Tramo tramoActualizado = tramoService.registrarFinTramo(idTramo);
            return ResponseEntity.ok(tramoActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ... (El constructor y los endpoints asignarCamion, iniciar, finalizar ya están aquí)

    /**
     * Endpoint para OBTENER TODOS los tramos.
     * URL: GET http://localhost:8080/api/v1/transporte/tramos
     */
    @GetMapping
    public ResponseEntity<List<Tramo>> getAllTramos() {
        return ResponseEntity.ok(tramoService.getAllTramos());
    }

    /**
     * Endpoint para CREAR un nuevo Tramo y asociarlo a una Ruta.
     * Resuelve: RF 4
     * URL: POST http://localhost:8080/api/v1/transporte/tramos?rutaId=1
     */
    @PostMapping
    public ResponseEntity<Tramo> createTramo(@RequestBody Tramo tramo, @RequestParam Long rutaId) {
        try {
            Tramo nuevoTramo = tramoService.createTramo(tramo, rutaId);
            return ResponseEntity.status(201).body(nuevoTramo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // (Mejorar manejo de error)
        }
    }

    /**
     * Endpoint para OBTENER un tramo por ID.
     * URL: GET http://localhost:8080/api/v1/transporte/tramos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tramo> getTramoById(@PathVariable Long id) {
        try {
            Tramo tramo = tramoService.getTramoById(id);
            return ResponseEntity.ok(tramo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}