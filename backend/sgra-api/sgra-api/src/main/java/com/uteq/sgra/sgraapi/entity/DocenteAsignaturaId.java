package com.uteq.sgra.sgraapi.entity;
import jakarta.persistence.*;

//import jakarta.persistence.Column;
//import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DocenteAsignaturaId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "iddocente")
    private Long iddocente;

    @Column(name = "idasignatura")
    private Integer idasignatura;

    @Column(name = "idperiodo")
    private Integer idperiodo;

    public DocenteAsignaturaId() {}

    public DocenteAsignaturaId(Long iddocente, Integer idasignatura, Integer idperiodo) {
        this.iddocente = iddocente;
        this.idasignatura = idasignatura;
        this.idperiodo = idperiodo;
    }

    public Long getIddocente() { return iddocente; }
    public void setIddocente(Long iddocente) { this.iddocente = iddocente; }

    public Integer getIdasignatura() { return idasignatura; }
    public void setIdasignatura(Integer idasignatura) { this.idasignatura = idasignatura; }

    public Integer getIdperiodo() { return idperiodo; }
    public void setIdperiodo(Integer idperiodo) { this.idperiodo = idperiodo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocenteAsignaturaId that)) return false;
        return Objects.equals(iddocente, that.iddocente)
                && Objects.equals(idasignatura, that.idasignatura)
                && Objects.equals(idperiodo, that.idperiodo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iddocente, idasignatura, idperiodo);
    }
}
