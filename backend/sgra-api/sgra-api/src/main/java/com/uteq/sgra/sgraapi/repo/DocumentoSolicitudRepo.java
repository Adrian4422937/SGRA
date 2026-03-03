package com.uteq.sgra.sgraapi.repo;

import com.uteq.sgra.sgraapi.entity.DocumentoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentoSolicitudRepo extends JpaRepository<DocumentoSolicitud, Integer> {
    List<DocumentoSolicitud> findByIdSolicitudOrderByFechaSubidaDesc(Integer idSolicitud);
    Optional<DocumentoSolicitud> findByIdDocumentoAndIdSolicitud(Integer idDocumento, Integer idSolicitud);
}
