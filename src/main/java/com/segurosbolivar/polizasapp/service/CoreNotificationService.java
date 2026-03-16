package com.segurosbolivar.polizasapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CoreNotificationService {

    private static final Logger log = LoggerFactory.getLogger(CoreNotificationService.class);

    public void notificarCore(String evento, Long polizaId) {
        log.info("[CORE-MOCK] Evento enviado al CORE -> evento='{}', polizaId={}", evento, polizaId);
    }
}
