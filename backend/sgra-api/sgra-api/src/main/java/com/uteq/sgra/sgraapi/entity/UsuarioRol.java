package com.uteq.sgra.sgraapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbusuariosroles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuariorol")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idrol", nullable = false)
    private Rol rol;

    @Column(name = "estado", nullable = false)
    private Boolean estado;
}
