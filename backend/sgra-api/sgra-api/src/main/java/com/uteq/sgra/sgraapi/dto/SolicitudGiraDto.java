package com.uteq.sgra.sgraapi.dto;

import java.time.LocalDate;

public record SolicitudGiraDto(
        Integer id,
        Integer idAsignatura,
        Integer idEmpresa,
        LocalDate fechaSolicitud,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String semana,
        Integer cupo_Maximo,
        String estadoActual
) {}
