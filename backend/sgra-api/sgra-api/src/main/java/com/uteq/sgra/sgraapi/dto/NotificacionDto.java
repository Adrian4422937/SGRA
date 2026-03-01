package com.uteq.sgra.sgraapi.dto;

import java.time.LocalDateTime;

public record NotificacionDto(
        String tipo,
        String mensaje,
        boolean leido,
        LocalDateTime fecha
) {}
