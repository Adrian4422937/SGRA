package com.uteq.sgra.sgraapi.service;

import com.uteq.sgra.sgraapi.dto.SolicitudGiraDto;
import com.uteq.sgra.sgraapi.repo.SolicitudGiraRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocenteMisGirasService {

    private final DocenteContext docenteContext;
    private final SolicitudGiraRepo solicitudGiraRepo;

    @Transactional(readOnly = true)
    public List<SolicitudGiraDto> listar(String login, String estado) {
        Long idDocente = docenteContext.requireIdDocente(login);

        List<String> estados;
        if (estado != null && !estado.isBlank()) {
            estados = List.of(estado.trim().toUpperCase());
        } else {
            estados = List.of("APROBADA", "EN_CURSO", "FINALIZADA");
        }

        var rows = solicitudGiraRepo.findMisGirasPorEstados(idDocente, estados);

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
