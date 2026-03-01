package com.uteq.sgra.sgraapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "tbgeneros")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Genero {
    @Id
    @Column(name = "idgenero")
    private Long id;

    @Column(name = "nombregenero", nullable = false, length = 20)
    private String nombreGenero;
}