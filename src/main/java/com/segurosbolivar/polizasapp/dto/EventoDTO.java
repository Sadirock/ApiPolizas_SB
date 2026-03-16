package com.segurosbolivar.polizasapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventoDTO(
        @NotBlank(message = "El evento es obligatorio")
        String evento,
        @NotNull(message = "El riesgo es obligatorio")
        Long polizaId
) {}
