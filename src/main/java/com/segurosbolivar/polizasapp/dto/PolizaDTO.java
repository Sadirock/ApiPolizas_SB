package com.segurosbolivar.polizasapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.segurosbolivar.polizasapp.entity.enums.EstadoPoliza;
import com.segurosbolivar.polizasapp.entity.enums.TipoPoliza;

public record PolizaDTO(
        Long id,
        String nombre,
        TipoPoliza tipo,
        EstadoPoliza estado,
        BigDecimal canon,
        BigDecimal prima,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        List<RiesgoDTO> riesgos) {
}
