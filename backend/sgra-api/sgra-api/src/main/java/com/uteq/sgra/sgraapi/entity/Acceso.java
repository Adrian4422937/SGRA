package com.uteq.sgra.sgraapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbaccesos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idacceso")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nombreusuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(name = "contrasenia", nullable = false, length = 255)
    private String contrasenia; // aquí guardas el BCrypt

    @Column(name = "estado", nullable = false)
    private Boolean estado;
}