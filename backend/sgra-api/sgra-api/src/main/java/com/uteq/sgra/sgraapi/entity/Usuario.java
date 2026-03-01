package com.uteq.sgra.sgraapi.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbusuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private Long idUsuario;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "identificacion", nullable = false, unique = true, length = 10)
    private String identificacion;

    @Column(name = "telefono", length = 10)
    private String telefono;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    // FK opcional: lo manejamos como ID para hacerlo simple
    @Column(name = "idgenero")
    private Long idGenero;

    @Column(name = "perfil_usuario", nullable = false, length = 30)
    private String perfilUsuario; // docente, estudiante, coordinador, admin, etc.
}