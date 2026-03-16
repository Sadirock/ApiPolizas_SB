package com.segurosbolivar.polizasapp.service;

import com.segurosbolivar.polizasapp.config.AppProperties;
import com.segurosbolivar.polizasapp.dto.PolizaDTO;
import com.segurosbolivar.polizasapp.dto.RiesgoDTO;
import com.segurosbolivar.polizasapp.dto.RiesgoRequest;
import com.segurosbolivar.polizasapp.exception.BusinessException;
import com.segurosbolivar.polizasapp.exception.ResourceNotFoundException;
import com.segurosbolivar.polizasapp.entity.Poliza;
import com.segurosbolivar.polizasapp.entity.Riesgo;
import com.segurosbolivar.polizasapp.entity.enums.EstadoPoliza;
import com.segurosbolivar.polizasapp.entity.enums.EstadoRiesgo;
import com.segurosbolivar.polizasapp.entity.enums.TipoPoliza;
import com.segurosbolivar.polizasapp.repository.PolizaRepository;
import com.segurosbolivar.polizasapp.repository.RiesgoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final AppProperties appProperties;
    private final CoreNotificationService coreNotification;

    public PolizaService(PolizaRepository polizaRepository,
            RiesgoRepository riesgoRepository,
            AppProperties appProperties,
            CoreNotificationService coreNotification) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
        this.appProperties = appProperties;
        this.coreNotification = coreNotification;
    }

    public List<PolizaDTO> listar(TipoPoliza tipo, EstadoPoliza estado) {
        List<Poliza> polizas;
        if (tipo != null && estado != null) {
            polizas = polizaRepository.findByTipoAndEstado(tipo, estado);
        } else if (tipo != null) {
            polizas = polizaRepository.findByTipo(tipo);
        } else if (estado != null) {
            polizas = polizaRepository.findByEstado(estado);
        } else {
            polizas = polizaRepository.findAll();
        }
        return polizas.stream().map(this::toDTO).toList();
    }

    public List<RiesgoDTO> listarRiesgos(Long polizaId) {
        if (!polizaRepository.existsById(polizaId)) {
            throw new ResourceNotFoundException("Póliza no encontrada con id: " + polizaId);
        }
        return riesgoRepository.findByPolizaId(polizaId)
                .stream()
                .map(this::toRiesgoDTO)
                .toList();
    }

    @Transactional
    public PolizaDTO renovar(Long polizaId) {
        Poliza poliza = findPolizaOrThrow(polizaId);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("No se puede renovar una póliza cancelada.");
        }

        BigDecimal factor = BigDecimal.ONE.add(BigDecimal.valueOf(appProperties.getIpc()));
        poliza.setCanon(poliza.getCanon().multiply(factor).setScale(2, RoundingMode.HALF_UP));
        poliza.setPrima(poliza.getPrima().multiply(factor).setScale(2, RoundingMode.HALF_UP));
        poliza.setEstado(EstadoPoliza.RENOVADA);

        PolizaDTO result = toDTO(polizaRepository.save(poliza));
        coreNotification.notificarCore("RENOVACION", polizaId);
        return result;
    }

    @Transactional
    public PolizaDTO cancelar(Long polizaId) {
        Poliza poliza = findPolizaOrThrow(polizaId);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("La póliza ya está cancelada.");
        }

        List<Riesgo> riesgos = riesgoRepository.findByPolizaId(polizaId);
        riesgos.forEach(r -> r.setEstado(EstadoRiesgo.CANCELADO));
        riesgoRepository.saveAll(riesgos);

        poliza.setEstado(EstadoPoliza.CANCELADA);
        PolizaDTO result = toDTO(polizaRepository.save(poliza));
        coreNotification.notificarCore("CANCELACION", polizaId);
        return result;
    }

    @Transactional
    public RiesgoDTO agregarRiesgo(Long polizaId, RiesgoRequest request) {
        Poliza poliza = findPolizaOrThrow(polizaId);

        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new BusinessException("Solo se pueden agregar riesgos a pólizas de tipo COLECTIVA.");
        }

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("No se pueden agregar riesgos a una póliza cancelada.");
        }

        Riesgo riesgo = new Riesgo();
        riesgo.setDescripcion(request.descripcion());
        riesgo.setAsegurado(request.asegurado());
        riesgo.setEstado(EstadoRiesgo.ACTIVO);
        riesgo.setPoliza(poliza);

        RiesgoDTO result = toRiesgoDTO(riesgoRepository.save(riesgo));
        coreNotification.notificarCore("AGREGAR_RIESGO", polizaId);
        return result;
    }

    private Poliza findPolizaOrThrow(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada con id: " + id));
    }

    private PolizaDTO toDTO(Poliza p) {
        List<RiesgoDTO> riesgosDTO = p.getRiesgos()
                .stream()
                .map(this::toRiesgoDTO)
                .toList();
        return new PolizaDTO(
                p.getId(), p.getNombre(), p.getTipo(), p.getEstado(),
                p.getCanon(), p.getPrima(), p.getFechaInicio(), p.getFechaFin(),
                riesgosDTO);
    }

    private RiesgoDTO toRiesgoDTO(Riesgo r) {
        return new RiesgoDTO(
                r.getId(), r.getDescripcion(), r.getAsegurado(),
                r.getEstado(), r.getPoliza().getId());
    }
}