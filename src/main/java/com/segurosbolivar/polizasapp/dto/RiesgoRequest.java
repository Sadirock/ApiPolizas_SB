package com.segurosbolivar.polizasapp.dto;

import jakarta.validation.constraints.NotBlank;

public record RiesgoRequest(
        @NotBlank(message = "La descripción es obligatoria") String descripcion,

        @NotBlank(message = "El asegurado es obligatorio") String asegurado) {
}
