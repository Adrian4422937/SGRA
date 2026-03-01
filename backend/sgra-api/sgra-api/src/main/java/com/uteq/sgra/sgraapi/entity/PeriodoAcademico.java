package com.uteq.sgra.sgraapi.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tbperiodo_academico", schema = "public")
public class PeriodoAcademico {

    @Id
    @Column(name = "idperiodo")
    private Integer id;

    @Column(name = "periodo", nullable = false)
    private String periodo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    public Integer getIdperiodo() { return id; }
    public void setIdperiodo(Integer id) { this.id = id; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
