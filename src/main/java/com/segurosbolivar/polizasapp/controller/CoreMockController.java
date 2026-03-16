package com.segurosbolivar.polizasapp.controller;

import com.segurosbolivar.polizasapp.dto.EventoDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    @PostMapping("/evento")
    public ResponseEntity<Map<String, String>> recibirEvento(@Valid @RequestBody EventoDTO evento) {
        log.info("[CORE-MOCK] Evento recibido -> evento='{}', polizaId={}",
                evento.evento(), evento.polizaId());
        return ResponseEntity.ok(Map.of(
                "mensaje", "Evento registrado correctamente",
                "evento", evento.evento(),
                "polizaId", String.valueOf(evento.polizaId())));
    }
}