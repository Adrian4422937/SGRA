package com.uteq.sgra.sgraapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record SolicitudGiraCreateRequest(
        @NotNull Integer idAsignatura,
        @NotNull Integer idEmpresa,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin,
        @NotBlank String semana,

        // Para que Angular "cupoMaximo" llegue bien:
        @NotNull @Min(1) @JsonProperty("cupo_Maximo") Integer cupo_Maximo
) {}