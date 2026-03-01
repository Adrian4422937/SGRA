package com.uteq.sgra.sgraapi.dto;

import java.time.LocalDate;

public record CoordinadorSolicitudDetalleDto(
        Integer idSolicitud,
        Long idDocente,
        String docente,
        Integer idAsignatura,
        String asignatura,
        Integer idEmpresa,
        String empresa,
        LocalDate fechaSolicitud,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String semana,
        Integer cupoMaximo,
        String estadoActual
) {}
