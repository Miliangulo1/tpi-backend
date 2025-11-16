package com.tpi.logistica.ms_tarifas.service;

import com.tpi.logistica.ms_tarifas.model.Tarifa;
import com.tpi.logistica.ms_tarifas.repository.TarifaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    public TarifaService(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    /**
     * Lógica de negocio para obtener todas las tarifas.
     */
    public List<Tarifa> getAllTarifas() {
        return tarifaRepository.findAll();
    }

    /**
     * Lógica de negocio para crear o actualizar una tarifa.
     */
    public Tarifa createOrUpdateTarifa(Tarifa tarifa) {
        // Aquí podríamos validar que el 'tipoTarifa' no venga vacío
        return tarifaRepository.save(tarifa);
    }

    // ... (El constructor y los métodos getAllTarifas() y createOrUpdateTarifa() ya están aquí)
    // (PD: createOrUpdateTarifa() ya nos sirve para el PUT, pero agreguemos los otros)

    /**
     * Lógica para OBTENER una tarifa por su ID.
     * (Requerido por el RF 10)
     */
    public Tarifa getTarifaById(Long id) {
        return tarifaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada con id: " + id));
    }

    /**
     * Lógica para ACTUALIZAR una tarifa existente.
     * (Requerido por el RF 10: "actualizar... tarifas")
     * (Podemos reutilizar el método 'createOrUpdate' si el ID ya existe)
     */
    public Tarifa updateTarifa(Long id, Tarifa tarifaDetails) {
        // 1. Nos aseguramos de que exista
        Tarifa tarifa = getTarifaById(id);

        // 2. Actualizamos los campos
        tarifa.setTipoTarifa(tarifaDetails.getTipoTarifa());
        tarifa.setValor(tarifaDetails.getValor());
        tarifa.setDescripcion(tarifaDetails.getDescripcion());

        // 3. Guardamos
        return tarifaRepository.save(tarifa);
    }

    /**
     * Lógica para BORRAR una tarifa.
     * (Requerido por el RF 10)
     */
    public void deleteTarifa(Long id) {
        // 1. Asegurarse de que exista
        Tarifa tarifa = getTarifaById(id);

        // 2. Borrarla
        tarifaRepository.delete(tarifa);
    }
}