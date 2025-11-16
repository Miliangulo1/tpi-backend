package com.tpi.logistica.ms_flota.service;

import com.tpi.logistica.ms_flota.model.Deposito;
import com.tpi.logistica.ms_flota.repository.DepositoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Le dice a Spring que esto es un Servicio
public class DepositoService {

    // Inyectamos el nuevo repositorio
    private final DepositoRepository depositoRepository;

    public DepositoService(DepositoRepository depositoRepository) {
        this.depositoRepository = depositoRepository;
    }

    public List<Deposito> getAllDepositos() {
        return depositoRepository.findAll();
    }

    public Deposito createDeposito(Deposito deposito) {
        // Aquí podríamos agregar validaciones (ej: que la lat/long no esté repetida)
        return depositoRepository.save(deposito);
    }

    public Deposito getDepositoById(Long id) {
        return depositoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Depósito no encontrado con id: " + id));
    }

    public Deposito updateDeposito(Long id, Deposito depositoDetails) {
        // 1. Asegurarse de que el depósito exista
        Deposito deposito = getDepositoById(id);

        // 2. Actualizar los campos
        deposito.setNombre(depositoDetails.getNombre());
        deposito.setDireccion(depositoDetails.getDireccion());
        deposito.setLatitud(depositoDetails.getLatitud());
        deposito.setLongitud(depositoDetails.getLongitud());
        deposito.setCostoEstadiaDiario(depositoDetails.getCostoEstadiaDiario());

        // 3. Guardar los cambios en la BD
        return depositoRepository.save(deposito);
    }

    public void deleteDeposito(Long id) {
        // 1. Asegurarse de que el depósito exista
        Deposito deposito = getDepositoById(id);

        // 2. Borrarlo
        depositoRepository.delete(deposito);
    }
}