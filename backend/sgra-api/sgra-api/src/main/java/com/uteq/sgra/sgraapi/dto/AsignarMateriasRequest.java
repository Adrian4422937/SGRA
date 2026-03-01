package com.uteq.sgra.sgraapi.dto;

import java.util.List;

public record AsignarMateriasRequest(
        Long docenteId,
        Integer periodoId,
        List<Integer> asignaturaIds
) {}
