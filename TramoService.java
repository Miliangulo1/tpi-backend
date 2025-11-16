package com.tpi.logistica.ms_transporte.service;

import com.tpi.logistica.ms_transporte.client.FlotaApiClient;
import com.tpi.logistica.ms_transporte.dto.CamionDTO;
import com.tpi.logistica.ms_transporte.model.*;
import com.tpi.logistica.ms_transporte.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TramoService {

    private final TramoRepository tramoRepository;
    private final RutaRepository rutaRepository;
    private final EstadoRepository estadoRepository;
    private final HistorialEstadoService historialEstadoService; // ¡Usamos tu servicio!
    private final FlotaApiClient flotaApiClient;
    private final SolicitudService solicitudService;

    public TramoService(TramoRepository tr, RutaRepository rr, EstadoRepository er,
                        HistorialEstadoService hes, // Inyectamos tu servicio
                        FlotaApiClient fa, SolicitudService ss) {
        this.tramoRepository = tr; this.rutaRepository = rr; this.estadoRepository = er;
        this.historialEstadoService = hes;
        this.flotaApiClient = fa; this.solicitudService = ss;
    }

    private Estado buscarEstado(String d) { return estadoRepository.findByDescripcion(d).orElseThrow(() -> new RuntimeException("Estado '" + d + "' no encontrado.")); }

    private void cambiarEstado(Solicitud s, Estado e, String obs) {
        s.setEstado(e);
        historialEstadoService.registrarCambioEstado(s, e, obs);
    }

    public List<Tramo> getAllTramos() { return tramoRepository.findAll(); }
    public Tramo getTramoById(Long id) { return tramoRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Tramo: " + id)); }

    public Tramo createTramo(Tramo t, Long rutaId) {
        Ruta r = rutaRepository.findById(rutaId).orElseThrow(() -> new NoSuchElementException("Ruta: " + rutaId));
        t.setRuta(r);
        Estado e = buscarEstado("ESTIMADO");
        t.setEstado(e);
        cambiarEstado(r.getSolicitud(), e, "Tramo creado: " + t.getOrigen() + " -> " + t.getDestino());
        return tramoRepository.save(t);
    }

    public Tramo asignarCamion(Long tramoId, Long camionId) {
        Tramo t = getTramoById(tramoId);
        CamionDTO c = flotaApiClient.getCamionById(camionId);
        if(c == null) throw new RuntimeException("Camión no encontrado");

        Contenedor cont = t.getRuta().getSolicitud().getContenedor();
        if(!c.isDisponible()) throw new IllegalStateException("Camión no disponible");
        if(c.getCapacidadPeso() < cont.getPeso()) throw new IllegalStateException("Peso excedido");
        if(c.getCapacidadVolumen() < cont.getVolumen()) throw new IllegalStateException("Volumen excedido");

        t.setCamionId(camionId);
        Estado e = buscarEstado("ASIGNADO");
        t.setEstado(e);
        cambiarEstado(t.getRuta().getSolicitud(), e, "Camión " + c.getPatente() + " asignado al tramo " + t.getId());

        flotaApiClient.marcarCamionOcupado(camionId);
        return tramoRepository.save(t);
    }

    public Tramo registrarInicioTramo(Long id) {
        Tramo t = getTramoById(id);
        if (!"ASIGNADO".equals(t.getEstado().getDescripcion())) {
            throw new IllegalStateException("El tramo no está 'ASIGNADO'.");
        }

        Estado e = buscarEstado("INICIADO");
        t.setEstado(e);
        t.setFechaHoraInicioReal(LocalDateTime.now());
        cambiarEstado(t.getRuta().getSolicitud(), e, "Tramo " + t.getId() + " iniciado.");

        return tramoRepository.save(t);
    }

    public Tramo registrarFinTramo(Long id) throws Exception {
        Tramo t = getTramoById(id);
        if (!"INICIADO".equals(t.getEstado().getDescripcion())) {
            throw new IllegalStateException("El tramo no está 'INICIADO'.");
        }

        Estado e = buscarEstado("FINALIZADO");
        t.setEstado(e);
        t.setFechaHoraFinReal(LocalDateTime.now());
        cambiarEstado(t.getRuta().getSolicitud(), e, "Tramo " + t.getId() + " finalizado.");

        if(t.getCamionId() != null) flotaApiClient.marcarCamionDisponible(t.getCamionId());

        Tramo saved = tramoRepository.save(t);

        List<Tramo> tramos = t.getRuta().getTramos();
        if(tramos.stream().allMatch(tr -> "FINALIZADO".equals(tr.getEstado().getDescripcion()))) {
            solicitudService.registrarCostoReal(t.getRuta().getSolicitud(), tramos);
        }
        return saved;
    }
}