package com.tpi.logistica.ms_flota.service;

import com.tpi.logistica.ms_flota.model.Camion;
import com.tpi.logistica.ms_flota.repository.CamionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// 1. @Service: Le dice a Spring que esta es una clase de Servicio
// y que debe crear una instancia de ella (un "Bean").
@Service
public class CamionService {

    // 2. Inyección de Dependencias (Constructor)
    // Le decimos a Spring que este servicio NECESITA el repositorio
    // que creamos antes.
    private final CamionRepository camionRepository;

    // 3. Spring automáticamente "inyectará" el CamionRepository aquí.
    public CamionService(CamionRepository camionRepository) {
        this.camionRepository = camionRepository;
    }

    // --- Aquí empieza nuestra lógica de negocio ---

    /**
     * Lógica de negocio para obtener todos los camiones.
     * Por ahora, es simple: solo le pide al repositorio que los traiga.
     */
    public List<Camion> getAllCamiones() {
        return camionRepository.findAll();
    }

    /**
     * Lógica de negocio para crear un nuevo camión.
     * (Más adelante, aquí agregaremos validaciones, como verificar
     * que la patente no esté duplicada).
     */
    public Camion createCamion(Camion camion) {
        // Por ahora, solo lo guardamos.
        return camionRepository.save(camion);
    }

    public Camion getCamionById(Long id) {
        // Busca el camión; si no lo encuentra, lanza una excepción (que se mapeará a un 404)
        return camionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Camión con ID " + id + " no encontrado en la flota."));
    }

    /**
     * Lógica para ACTUALIZAR un camión existente.
     * (Requerido por el RF 10: "actualizar... camiones")
     */
    public Camion updateCamion(Long id, Camion camionDetails) {
        // 1. Asegurarse de que el camión exista (reutilizamos el método anterior)
        Camion camion = getCamionById(id);

        // 2. Actualizar los campos
        camion.setPatente(camionDetails.getPatente());
        camion.setNombreTransportista(camionDetails.getNombreTransportista());
        camion.setTelefono(camionDetails.getTelefono());
        camion.setCapacidadPeso(camionDetails.getCapacidadPeso());
        camion.setCapacidadVolumen(camionDetails.getCapacidadVolumen());
        camion.setCostoPorKm(camionDetails.getCostoPorKm());
        camion.setConsumoCombustiblePromedio(camionDetails.getConsumoCombustiblePromedio());
        camion.setDisponible(camionDetails.isDisponible());

        // 3. Guardar los cambios en la BD
        return camionRepository.save(camion);
    }

    /**
     * Lógica para BORRAR un camión.
     * (Requerido por el RF 10)
     */
    public void deleteCamion(Long id) {
        // 1. Asegurarse de que el camión exista
        Camion camion = getCamionById(id);

        // 2. Borrarlo
        camionRepository.delete(camion);
    }
    /**
     * Lógica para marcar un camión como OCUPADO (no disponible).
     * Esto será llamado por ms_transporte después de una asignación.
     */
    public Camion marcarCamionOcupado(Long id) {
        Camion camion = getCamionById(id); // Reutilizamos el método que ya existe
        camion.setDisponible(false);
        return camionRepository.save(camion);
    }

    /**
     * Lógica para marcar un camión como DISPONIBLE (libre).
     * Esto será llamado por ms_transporte cuando un tramo finalice.
     */
    public Camion marcarCamionDisponible(Long id) {
        Camion camion = getCamionById(id); // Reutilizamos el método que ya existe
        camion.setDisponible(true);
        return camionRepository.save(camion);
    }
    // ... dentro de CamionService

    public List<Camion> getCamionesAptos(double peso, double volumen) {
        return camionRepository.findByDisponibleTrueAndCapacidadPesoGreaterThanEqualAndCapacidadVolumenGreaterThanEqual(peso, volumen);
    }
}