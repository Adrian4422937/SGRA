package com.uteq.sgra.sgraapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbdocumento_solicitud")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DocumentoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddocumento")
    private Integer idDocumento;

    @Column(name = "idsolicitud", nullable = false)
    private Integer idSolicitud;

    @Column(name = "tipo_documento", length = 50, nullable = false)
    private String tipoDocumento;

    @Column(name = "archivo", length = 255, nullable = false)
    private String archivo; // ruta/filename guardado

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;
}
