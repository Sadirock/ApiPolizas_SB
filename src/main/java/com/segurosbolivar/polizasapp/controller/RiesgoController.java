package com.segurosbolivar.polizasapp.controller;


import com.segurosbolivar.polizasapp.dto.RiesgoDTO;
import com.segurosbolivar.polizasapp.service.RiesgoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
public class RiesgoController {

    private final RiesgoService riesgoService;

    public RiesgoController(RiesgoService riesgoService) {
        this.riesgoService = riesgoService;
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<RiesgoDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(riesgoService.cancelar(id));
    }
}