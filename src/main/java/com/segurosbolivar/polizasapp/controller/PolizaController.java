package com.segurosbolivar.polizasapp.controller;

import com.segurosbolivar.polizasapp.dto.PolizaDTO;
import com.segurosbolivar.polizasapp.dto.RiesgoDTO;
import com.segurosbolivar.polizasapp.dto.RiesgoRequest;
import com.segurosbolivar.polizasapp.entity.enums.EstadoPoliza;
import com.segurosbolivar.polizasapp.entity.enums.TipoPoliza;
import com.segurosbolivar.polizasapp.service.PolizaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    @GetMapping
    public ResponseEntity<List<PolizaDTO>> listar(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        return ResponseEntity.ok(polizaService.listar(tipo, estado));
    }

    //Ver riesgos asociados
    @GetMapping("/{id}/riesgos")
    public ResponseEntity<List<RiesgoDTO>> listarRiesgos(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.listarRiesgos(id));
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<PolizaDTO> renovar(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.renovar(id));
    }
    
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PolizaDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.cancelar(id));
    }

    @PostMapping("/{id}/riesgos")
    public ResponseEntity<RiesgoDTO> agregarRiesgo(
            @PathVariable Long id,
            @Valid @RequestBody RiesgoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(polizaService.agregarRiesgo(id, request));
    }
}