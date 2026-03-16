package com.segurosbolivar.polizasapp.service;

import com.segurosbolivar.polizasapp.dto.RiesgoDTO;
import com.segurosbolivar.polizasapp.exception.BusinessException;
import com.segurosbolivar.polizasapp.exception.ResourceNotFoundException;
import com.segurosbolivar.polizasapp.entity.Riesgo;
import com.segurosbolivar.polizasapp.entity.enums.EstadoRiesgo;
import com.segurosbolivar.polizasapp.entity.enums.TipoPoliza;
import com.segurosbolivar.polizasapp.repository.RiesgoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;
    private final CoreNotificationService coreNotification;

    public RiesgoService(RiesgoRepository riesgoRepository,
            CoreNotificationService coreNotification) {
        this.riesgoRepository = riesgoRepository;
        this.coreNotification = coreNotification;
    }

    @Transactional
    public RiesgoDTO cancelar(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new ResourceNotFoundException("Riesgo no encontrado con id: " + riesgoId));

        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO) {
            throw new BusinessException("El riesgo ya está cancelado.");
        }

        if (riesgo.getPoliza().getTipo() == TipoPoliza.INDIVIDUAL) {
            long activos = riesgoRepository
                    .findByPolizaIdAndEstado(riesgo.getPoliza().getId(), EstadoRiesgo.ACTIVO)
                    .size();
            if (activos <= 1) {
                throw new BusinessException(
                        "No se puede cancelar el único riesgo activo de una póliza INDIVIDUAL. " +
                                "Cancele la póliza directamente.");
            }
        }

        riesgo.setEstado(EstadoRiesgo.CANCELADO);
        RiesgoDTO result = toDTO(riesgoRepository.save(riesgo));
        coreNotification.notificarCore("CANCELAR_RIESGO", riesgo.getPoliza().getId());
        return result;
    }

    private RiesgoDTO toDTO(Riesgo r) {
        return new RiesgoDTO(
                r.getId(), r.getDescripcion(), r.getAsegurado(),
                r.getEstado(), r.getPoliza().getId());
    }
}
