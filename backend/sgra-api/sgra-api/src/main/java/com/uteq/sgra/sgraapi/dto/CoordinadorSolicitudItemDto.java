package com.uteq.sgra.sgraapi.dto;

import java.time.LocalDate;

public record CoordinadorSolicitudItemDto(
        Integer idSolicitud,
        String docente,
        String asignatura,
        String empresa,
        LocalDate fechaSolicitud,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String semana,
        Integer cupoMaximo,
        String estadoActual
) {}