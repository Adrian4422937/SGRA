package com.uteq.sgra.sgraapi.dto;

public record MateriaAsignadaDto(
        Integer idAsignatura,
        String nombreAsignatura,
        Integer creditos,
        Integer idPeriodo,
        String periodo
) {}