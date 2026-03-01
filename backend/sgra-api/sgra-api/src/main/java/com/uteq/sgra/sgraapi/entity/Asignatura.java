package com.uteq.sgra.sgraapi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbasignatura")
public class Asignatura {
    @Id
    @Column(name = "idasignatura")
    private Integer idasignatura;

    @Column(name = "nombreasignatura", nullable = false)
    private String nombre;

    @Column(name = "creditos", nullable = false)
    private Integer creditos;

    public Integer getIdasignatura() { return idasignatura; }
    public void setIdasignatura(Integer idasignatura) { this.idasignatura = idasignatura; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getCreditos() { return creditos; }
    public void setCreditos(Integer creditos) { this.creditos = creditos; }
}