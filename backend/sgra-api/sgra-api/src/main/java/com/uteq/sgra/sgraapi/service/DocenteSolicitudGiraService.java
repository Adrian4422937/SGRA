package com.uteq.sgra.sgraapi.service;

import com.uteq.sgra.sgraapi.repo.AccesoRepo;
import com.uteq.sgra.sgraapi.dto.SolicitudGiraCreateRequest;
import com.uteq.sgra.sgraapi.dto.SolicitudGiraDto;
import com.uteq.sgra.sgraapi.entity.SolicitudGira;
import com.uteq.sgra.sgraapi.repo.SolicitudGiraRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocenteSolicitudGiraService {

    private final AccesoRepo accesoRepo;
    private final SolicitudGiraRepo solicitudGiraRepo;


    // ✅ reutiliza tu lógica actual para obtener el "idDocente" desde username
    private Long obtenerIdDocenteDesdeUsername(String username) {
        var acceso = accesoRepo.findFirstByNombreUsuarioIgnoreCaseAndEstadoTrue(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Acceso inválido"));

        // OJO: aquí estás usando idUsuario como idDocente (igual que en tu crear()).
        // Lo dejamos así para no romper tu flujo actual.
        return acceso.getUsuario().getIdUsuario();
    }

    @Transactional
    public SolicitudGiraDto crear(String username, SolicitudGiraCreateRequest req) {

        Long idDocente = obtenerIdDocenteDesdeUsername(username);

        if (req.fechaFin().isBefore(req.fechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaFin no puede ser menor a fechaInicio");
        }

        SolicitudGira s = solicitudGiraRepo.save(SolicitudGira.builder()
                .idDocente(idDocente)
                .idAsignatura(req.idAsignatura())
                .idEmpresa(req.idEmpresa())
                .fechaSolicitud(LocalDate.now())
                .fechaInicio(req.fechaInicio())
                .fechaFin(req.fechaFin())
                .semana(req.semana())
                .cupo_Maximo(req.cupo_Maximo())
                .estadoActual("PENDIENTE")
                .build());

        return new SolicitudGiraDto(
                s.getId(),
                s.getIdAsignatura(),
                s.getIdEmpresa(),
                s.getFechaSolicitud(),
                s.getFechaInicio(),
                s.getFechaFin(),
                s.getSemana(),
                s.getCupo_Maximo(),
                s.getEstadoActual()
        );
    }

    // ✅ NUEVO: listar solicitudes del docente con estado real
    @Transactional(readOnly = true)
    public List<SolicitudGiraDto> listarMisSolicitudes(String username) {
        Long idDocente = obtenerIdDocenteDesdeUsername(username);

        return solicitudGiraRepo.findByIdDocenteOrderByIdDesc(idDocente)
                .stream()
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

    // ✅ NUEVO: stats reales desde BD
    @Transactional(readOnly = true)
    public Map<String, Long> obtenerStats(String username) {
        Long idDocente = obtenerIdDocenteDesdeUsername(username);

        long enviadas = solicitudGiraRepo.countByIdDocente(idDocente);
        long aprobadas = solicitudGiraRepo.countByIdDocenteAndEstadoActualContainingIgnoreCase(idDocente, "APROB");
        long pendientes = solicitudGiraRepo.countByIdDocenteAndEstadoActualContainingIgnoreCase(idDocente, "PEND");
        long rechazadas = solicitudGiraRepo.countByIdDocenteAndEstadoActualContainingIgnoreCase(idDocente, "RECHAZ");

        return Map.of(
                "enviadas", enviadas,
                "aprobadas", aprobadas,
                "pendientes", pendientes,
                "rechazadas", rechazadas
        );
    }
}