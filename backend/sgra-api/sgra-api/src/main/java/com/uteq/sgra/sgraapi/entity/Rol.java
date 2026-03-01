package com.uteq.sgra.sgraapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbroles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idrol")
    private Long id;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol; // ROLE_ADMIN, ROLE_USER

    @Column(name = "estado", nullable = false)
    private Boolean estado;
}