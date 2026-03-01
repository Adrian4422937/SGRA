package com.uteq.sgra.sgraapi.dto;

public record CoordinadorDashboardResumenDto(
        long pendientes,
        long aprobadas,
        long rechazadas,
        long enCurso,
        long finalizadas
) {}
