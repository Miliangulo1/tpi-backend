package com.tpi.logistica.ms_transporte.service;

import com.tpi.logistica.ms_transporte.client.*;
import com.tpi.logistica.ms_transporte.dto.*;
import com.tpi.logistica.ms_transporte.model.*;
import com.tpi.logistica.ms_transporte.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolicitudService {

    // Dependencias
    private final SolicitudRepository solicitudRepository;
    private final ClienteRepository clienteRepository;
    private final ContenedorRepository contenedorRepository;
    private final EstadoRepository estadoRepository;
    private final HistorialEstadoService historialEstadoService; // ¡Usamos tu servicio!
    private final TarifasApiClient tarifasApiClient;
    private final GoogleMapsApiClient googleMapsApiClient;
    private final FlotaApiClient flotaApiClient;

    // Constructor Actualizado
    public SolicitudService(SolicitudRepository sr, ClienteRepository cr, ContenedorRepository cor,
                            EstadoRepository er,
                            HistorialEstadoService hes,
                            TarifasApiClient ta, GoogleMapsApiClient ga, FlotaApiClient fa) {
        this.solicitudRepository = sr; this.clienteRepository = cr; this.contenedorRepository = cor;
        this.estadoRepository = er;
        this.historialEstadoService = hes;
        this.tarifasApiClient = ta; this.googleMapsApiClient = ga; this.flotaApiClient = fa;
    }

    // --- MÉTODOS AUXILIARES ---
    private Estado buscarEstado(String descripcion) {
        return estadoRepository.findByDescripcion(descripcion)
                .orElseThrow(() -> new RuntimeException("Estado '" + descripcion + "' no encontrado en la BD."));
    }

    private void cambiarEstado(Solicitud solicitud, Estado nuevoEstado, String observaciones) {
        solicitud.setEstado(nuevoEstado);
        historialEstadoService.registrarCambioEstado(solicitud, nuevoEstado, observaciones);
    }

    // --- MÉTODOS CRUD BÁSICOS ---

    @Transactional(readOnly = true)
    public List<Solicitud> getAllSolicitudes() {
        return solicitudRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Solicitud getSolicitudById(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada con id: " + id));
    }

    public Solicitud updateSolicitud(Long id, Solicitud solicitudDetails) {
        Solicitud solicitud = getSolicitudById(id);

        if (solicitudDetails.getEstado() != null && !solicitud.getEstado().getId().equals(solicitudDetails.getEstado().getId())) {
            Estado nuevoEstado = estadoRepository.findById(solicitudDetails.getEstado().getId())
                    .orElseThrow(() -> new RuntimeException("Estado inválido proporcionado."));
            cambiarEstado(solicitud, nuevoEstado, "Estado actualizado manualmente por Operador");
        }

        solicitud.setCostoEstimado(solicitudDetails.getCostoEstimado());
        solicitud.setTiempoEstimado(solicitudDetails.getTiempoEstimado());
        return solicitudRepository.save(solicitud);
    }

    public void deleteSolicitud(Long id) {
        Solicitud solicitud = getSolicitudById(id);
        solicitudRepository.delete(solicitud);
    }

    // --- LÓGICA DE NEGOCIO ---

    public Solicitud createSolicitud(Solicitud solicitud) throws Exception {
        Optional<Cliente> clienteExistente = clienteRepository.findByEmail(solicitud.getCliente().getEmail());
        Cliente cliente = clienteExistente.orElseGet(() -> clienteRepository.save(solicitud.getCliente()));
        solicitud.setCliente(cliente);
        solicitud.getContenedor().setCliente(cliente);
        solicitud.setContenedor(contenedorRepository.save(solicitud.getContenedor()));

        Estado estadoBorrador = buscarEstado("BORRADOR");
        solicitud.setEstado(estadoBorrador);

        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);

        try {
            calcularEstimaciones(solicitudGuardada);
        } catch (Exception e) {
            System.err.println("Error al calcular estimaciones: " + e.getMessage());
        }

        // Usamos tu servicio para registrar el historial
        cambiarEstado(solicitudGuardada, estadoBorrador, "Solicitud creada");

        return solicitudRepository.save(solicitudGuardada);
    }

    public Solicitud calcularRutaTentativa(Solicitud solicitud) throws Exception {
        calcularEstimaciones(solicitud);
        return solicitud;
    }

    public List<Solicitud> getSolicitudesPendientes() {
        return solicitudRepository.findByEstado_DescripcionNotIn(List.of("ENTREGADA", "CANCELADA"));
    }

    @Transactional(readOnly = true)
    public SeguimientoDTO getSeguimiento(Long id) {
        Solicitud solicitud = getSolicitudById(id);
        // Forzar inicialización de relaciones LAZY antes de cerrar la transacción
        if (solicitud.getRutas() != null) {
            solicitud.getRutas().size(); // Inicializar la colección
        }
        List<HistorialEstado> historial = historialEstadoService.getHistorialPorSolicitud(id);
        return new SeguimientoDTO(solicitud, historial);
    }

    public void registrarCostoReal(Solicitud solicitud, List<Tramo> tramos) throws Exception {

        if (tramos.isEmpty()) {
            throw new IllegalStateException("No se pueden calcular costos reales sin tramos.");
        }

        double costoFinalTotal = 0.0;
        long tiempoRealTotalSegundos = 0;

        // (El resto de tu lógica de cálculo de tarifas y Google Maps está perfecta)
        Map<String, Double> mapaTarifas = tarifasApiClient.getAllTarifas().stream()
                .collect(Collectors.toMap(TarifaDTO::getTipoTarifa, TarifaDTO::getValor));

        Double cargoGestion = mapaTarifas.getOrDefault("CARGO_GESTION_TRAMO", 0.0);
        Double precioCombustible = mapaTarifas.getOrDefault("PRECIO_COMBUSTIBLE_LITRO", 0.0);
        Double costoEstadia = mapaTarifas.getOrDefault("COSTO_ESTADIA_DEPOSITO_DIA", 0.0);

        for (Tramo tramo : tramos) {
            costoFinalTotal += cargoGestion;
            if (tramo.getFechaHoraFinReal() != null && tramo.getFechaHoraInicioReal() != null) {
                tiempoRealTotalSegundos += ChronoUnit.SECONDS.between(tramo.getFechaHoraInicioReal(), tramo.getFechaHoraFinReal());
            }
            CamionDTO camion = flotaApiClient.getCamionById(tramo.getCamionId());
            DistanciaDTO distancia = googleMapsApiClient.calcularDistancia(tramo.getOrigen(), tramo.getDestino());

            if (camion != null && distancia != null) {
                double kms = distancia.getKilometros();
                costoFinalTotal += (camion.getCostoPorKm() * kms) +
                        (camion.getConsumoCombustiblePromedio() * kms * precioCombustible);
            }
            if (tramo.getTipo() != null && tramo.getTipo().contains("deposito")) {
                long dias = 1;
                if (tramo.getFechaHoraFinReal() != null) {
                    dias = Math.max(1, ChronoUnit.DAYS.between(tramo.getFechaHoraInicioReal(), tramo.getFechaHoraFinReal()));
                }
                costoFinalTotal += (dias * costoEstadia);
            }
        }
        solicitud.setCostoFinal(costoFinalTotal);
        solicitud.setTiempoReal(tiempoRealTotalSegundos / 3600.0);
        cambiarEstado(solicitud, buscarEstado("ENTREGADA"), "Envío completado");
        solicitudRepository.save(solicitud);
    }

    private void calcularEstimaciones(Solicitud solicitud) throws Exception {
        String origen = solicitud.getOrigenLatitud() + "," + solicitud.getOrigenLongitud();
        String destino = solicitud.getDestinoLatitud() + "," + solicitud.getDestinoLongitud();
        DistanciaDTO distancia = googleMapsApiClient.calcularDistancia(origen, destino);
        if (distancia == null) throw new RuntimeException("Error Google Maps");
        solicitud.setTiempoEstimado(distancia.getDuracionSegundos() / 3600.0);

        List<TarifaDTO> tarifas = tarifasApiClient.getAllTarifas();
        Map<String, Double> mapaTarifas = tarifas.stream()
                .collect(Collectors.toMap(TarifaDTO::getTipoTarifa, TarifaDTO::getValor));
        List<CamionDTO> aptos = flotaApiClient.getCamionesAptos(solicitud.getContenedor().getPeso(), solicitud.getContenedor().getVolumen());

        double promConsumo = aptos.stream().mapToDouble(CamionDTO::getConsumoCombustiblePromedio).average().orElse(0.4);
        double promCostoKm = aptos.stream().mapToDouble(CamionDTO::getCostoPorKm).average().orElse(0.5);
        double kms = distancia.getKilometros();
        int cantidadTramosEstimados = 1;
        Double cargoGestionPorTramo = mapaTarifas.getOrDefault("CARGO_GESTION_TRAMO", 0.0);

        double costo = (cargoGestionPorTramo * cantidadTramosEstimados) +
                (promCostoKm * kms) +
                (promConsumo * kms * mapaTarifas.getOrDefault("PRECIO_COMBUSTIBLE_LITRO", 0.0));
        solicitud.setCostoEstimado(costo);
    }
}