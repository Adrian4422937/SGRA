package com.uteq.sgra.sgraapi.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudGiraEstadoDto {
    private Integer idSolicitud;
    private Integer idAsignatura;
    private Integer idEmpresa;
    private LocalDate fechaSolicitud;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String semana;
    private Integer cupoMaximo;
    private String estadoActual;
}
