package com.uteq.sgra.sgraapi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbdocente_asignatura", schema = "public")
public class DocenteAsignatura {

    @EmbeddedId
    private DocenteAsignaturaId id;

    @MapsId("iddocente")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddocente", referencedColumnName = "idusuario")
    private Usuario docente;

    @MapsId("idasignatura")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idasignatura", referencedColumnName = "idasignatura")
    private Asignatura asignatura;

    @MapsId("idperiodo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idperiodo", referencedColumnName = "idperiodo")
    private PeriodoAcademico periodo;

    public DocenteAsignatura() {}

    public DocenteAsignatura(Usuario docente, Asignatura asignatura, PeriodoAcademico periodo) {
        this.docente = docente;
        this.asignatura = asignatura;
        this.periodo = periodo;

        this.id = new DocenteAsignaturaId(
                docente.getIdUsuario(),
                asignatura.getIdasignatura(),
                periodo.getIdperiodo()
        );
    }

    public DocenteAsignaturaId getId() { return id; }
    public void setId(DocenteAsignaturaId id) { this.id = id; }

    public Usuario getDocente() { return docente; }
    public void setDocente(Usuario docente) { this.docente = docente; }

    public Asignatura getAsignatura() { return asignatura; }
    public void setAsignatura(Asignatura asignatura) { this.asignatura = asignatura; }

    public PeriodoAcademico getPeriodo() { return periodo; }
    public void setPeriodo(PeriodoAcademico periodo) { this.periodo = periodo; }
}
