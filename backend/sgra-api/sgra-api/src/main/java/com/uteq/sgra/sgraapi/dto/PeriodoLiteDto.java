package com.uteq.sgra.sgraapi.dto;

import java.time.LocalDate;

public record PeriodoLiteDto(
        Integer id,
        String periodo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado
) {}