package com.uteq.sgra.sgraapi.service;

import com.uteq.sgra.sgraapi.dto.SolicitudGiraDto;
import com.uteq.sgra.sgraapi.entity.SolicitudGira;
import com.uteq.sgra.sgraapi.repo.SolicitudGiraRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocenteEstadoSolicitudesService {

    private final DocenteContext docenteContext;
    private final SolicitudGiraRepo solicitudGiraRepo;

    @Transactional(readOnly = true)
    public List<SolicitudGiraDto> listar(String username, Integer idAsignatura, String estado) {
        Long idDocente = docenteContext.requireIdDocente(username);

        String estadoFiltro = (estado == null || estado.isBlank()) ? null : estado.trim();

        List<SolicitudGira> rows;

        if (idAsignatura != null && estadoFiltro != null) {
            rows = solicitudGiraRepo.findByIdDocenteAndIdAsignaturaAndEstadoActualIgnoreCaseOrderByIdDesc(
                    idDocente, idAsignatura, estadoFiltro
            );
        } else if (idAsignatura != null) {
            rows = solicitudGiraRepo.findByIdDocenteAndIdAsignaturaOrderByIdDesc(
                    idDocente, idAsignatura
            );
        } else if (estadoFiltro != null) {
            rows = solicitudGiraRepo.findByIdDocenteAndEstadoActualIgnoreCaseOrderByIdDesc(
                    idDocente, estadoFiltro
            );
        } else {
            rows = solicitudGiraRepo.findByIdDocenteOrderByIdDesc(idDocente);
        }

        return rows.stream()
                .map(s -> new SolicitudGiraDto(
                        s.getId(),
                        s.getIdAsignatura(),
                        s.getIdEmpresa(),
                        s.getFechaSolicitud(),
                        s.getFechaInicio(),
                        s.getFechaFin(),
                        s.getSemana(),
                        s.getCupo_Maximo(),
                        s.getEstadoActual()
                ))
                .toList();
    }
}