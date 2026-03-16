package com.segurosbolivar.polizasapp.dto;

import com.segurosbolivar.polizasapp.entity.enums.EstadoRiesgo;

public record RiesgoDTO(
        Long id,
        String descripcion,
        String asegurado,
        EstadoRiesgo estado,
        Long polizaId) {
}
