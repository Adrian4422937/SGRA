package com.uteq.sgra.sgraapi.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;


@Entity
@Table(name = "tbsolicitud_gira")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SolicitudGira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsolicitud")
    private Integer id;

    @Column(name = "iddocente", nullable = false)
    private Long idDocente;

    @Column(name = "idasignatura", nullable = false)
    private Integer idAsignatura;

    @Column(name = "idempresa", nullable = false)
    private Integer idEmpresa;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @NotBlank
    @Column(name = "semana", length = 20, nullable = false)
    private String semana;

    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupo_Maximo;

    @NotNull
    @Column(name = "estado_actual", length = 30, nullable = false)
    private String estadoActual;
}