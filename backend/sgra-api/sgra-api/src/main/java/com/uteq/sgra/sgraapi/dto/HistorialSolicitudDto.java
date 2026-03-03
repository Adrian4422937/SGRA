package com.uteq.sgra.sgraapi.dto;

import java.time.LocalDateTime;

public record HistorialSolicitudDto(
        Integer idHistorial,
        Integer idSolicitud,
        Long idPersona,
        String persona,
        String accion,
        String observacion,
        LocalDateTime fecha
) {}
