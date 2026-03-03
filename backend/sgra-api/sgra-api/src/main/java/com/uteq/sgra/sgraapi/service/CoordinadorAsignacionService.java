package com.uteq.sgra.sgraapi.service;

import com.uteq.sgra.sgraapi.dto.AsignarMateriasRequest;
import com.uteq.sgra.sgraapi.entity.*;
import com.uteq.sgra.sgraapi.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uteq.sgra.sgraapi.dto.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import com.uteq.sgra.sgraapi.dto.HistorialSolicitudDto;

@Service
public class CoordinadorAsignacionService {

    private final UsuarioRepo usuarioRepo;
    private final AsignaturaRepo asignaturaRepo;
    private final PeriodoAcademicoRepo periodoRepo;
    private final DocenteAsignaturaRepo docenteAsignaturaRepo;

    // ✅ nuevos
    private final SolicitudGiraRepo solicitudGiraRepo;
    private final AccesoRepo accesoRepo;

    public CoordinadorAsignacionService(
            UsuarioRepo usuarioRepo,
            AsignaturaRepo asignaturaRepo,
            PeriodoAcademicoRepo periodoRepo,
            DocenteAsignaturaRepo docenteAsignaturaRepo,
            SolicitudGiraRepo solicitudGiraRepo,
            AccesoRepo accesoRepo
    ) {
        this.usuarioRepo = usuarioRepo;
        this.asignaturaRepo = asignaturaRepo;
        this.periodoRepo = periodoRepo;
        this.docenteAsignaturaRepo = docenteAsignaturaRepo;
        this.solicitudGiraRepo = solicitudGiraRepo;
        this.accesoRepo = accesoRepo;
    }

    // =========================
    // YA TENÍAS ESTO (SE MANTIENE)
    // =========================
    @Transactional
    public void asignar(AsignarMateriasRequest req) {
        if (!usuarioRepo.isDocente(req.docenteId())) {
            throw new RuntimeException("El usuario seleccionado no tiene rol DOCENTE");
        }

        Usuario docente = usuarioRepo.findById(req.docenteId())
                .orElseThrow(() -> new RuntimeException("Docente no existe"));

        PeriodoAcademico periodo = periodoRepo.findById(req.periodoId())
                .orElseThrow(() -> new RuntimeException("Periodo no existe"));

        for (Integer asigId : req.asignaturaIds()) {
            Asignatura asig = asignaturaRepo.findById(asigId)
                    .orElseThrow(() -> new RuntimeException("Asignatura no existe: " + asigId));

            DocenteAsignaturaId id = new DocenteAsignaturaId(
                    docente.getIdUsuario(),
                    asig.getIdasignatura(),
                    periodo.getIdperiodo()
            );

            if (!docenteAsignaturaRepo.existsById(id)) {
                docenteAsignaturaRepo.save(new DocenteAsignatura(docente, asig, periodo));
            }
        }
    }

    // =========================
    // NUEVO: Dashboard coordinador
    // =========================
    @Transactional(readOnly = true)
    public CoordinadorDashboardResumenDto resumenDashboard() {
        long pendientes = solicitudGiraRepo.countByEstadoActualIgnoreCase("PENDIENTE");
        long aprobadas = solicitudGiraRepo.countByEstadoActualIgnoreCase("APROBADA");
        long rechazadas = solicitudGiraRepo.countByEstadoActualIgnoreCase("RECHAZADA");
        long enCurso = solicitudGiraRepo.countByEstadoActualIgnoreCase("EN_CURSO");
        long finalizadas = solicitudGiraRepo.countByEstadoActualIgnoreCase("FINALIZADA");

        return new CoordinadorDashboardResumenDto(
                pendientes, aprobadas, rechazadas, enCurso, finalizadas
        );
    }

    // =========================
    // NUEVO: Bandeja de solicitudes
    // =========================
    @Transactional(readOnly = true)
    public List<CoordinadorSolicitudItemDto> listarSolicitudes(String estado) {
        return solicitudGiraRepo.findBandejaCoordinador(estado).stream()
                .map(r -> new CoordinadorSolicitudItemDto(
                        r.getIdSolicitud(),
                        r.getDocente(),
                        r.getAsignatura(),
                        r.getEmpresa(),
                        r.getFechaSolicitud(),
                        r.getFechaInicio(),
                        r.getFechaFin(),
                        r.getSemana(),
                        r.getCupoMaximo(),
                        r.getEstadoActual()
                ))
                .toList();
    }

    // =========================
    // NUEVO: Detalle de solicitud
    // =========================
    @Transactional(readOnly = true)
    public CoordinadorSolicitudDetalleDto detalleSolicitud(Integer id) {
        var r = solicitudGiraRepo.findDetalleCoordinador(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + id));

        return new CoordinadorSolicitudDetalleDto(
                r.getIdSolicitud(),
                r.getIdDocente(),
                r.getDocente(),
                r.getIdAsignatura(),
                r.getAsignatura(),
                r.getIdEmpresa(),
                r.getEmpresa(),
                r.getFechaSolicitud(),
                r.getFechaInicio(),
                r.getFechaFin(),
                r.getSemana(),
                r.getCupoMaximo(),
                r.getEstadoActual()
        );
    }

    // =========================
    // NUEVO: Aprobar solicitud
    // =========================
    @Transactional
    public void aprobarSolicitud(Integer idSolicitud, String observacion) {
        cambiarEstadoConTrazabilidad(idSolicitud, "APROBADA", "APROBACION_COORDINADOR", observacion);
    }

    // =========================
    // NUEVO: Rechazar solicitud
    // =========================
    @Transactional
    public void rechazarSolicitud(Integer idSolicitud, String observacion) {
        cambiarEstadoConTrazabilidad(idSolicitud, "RECHAZADA", "RECHAZO_COORDINADOR", observacion);
    }

    // =========================
    // Helper interno
    // =========================
    private void cambiarEstadoConTrazabilidad(Integer idSolicitud, String nuevoEstado, String accion, String observacion) {
        SolicitudGira solicitud = solicitudGiraRepo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + idSolicitud));

        int updated = solicitudGiraRepo.actualizarEstado(idSolicitud, nuevoEstado);
        if (updated == 0) {
            throw new RuntimeException("No se pudo actualizar el estado de la solicitud " + idSolicitud);
        }

        Long idCoordinador = getUsuarioActualId();

        solicitudGiraRepo.insertarHistorial(
                idSolicitud,
                idCoordinador,
                accion,
                (observacion == null || observacion.isBlank()) ? "-" : observacion
        );

        String mensaje = "Tu solicitud de gira #" + idSolicitud + " fue " + nuevoEstado.toLowerCase() + " por Coordinación.";

        solicitudGiraRepo.insertarNotificacion(
                solicitud.getIdDocente(),
                "SOLICITUD_GIRA",
                mensaje
        );
    }

    private Long getUsuarioActualId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var acceso = accesoRepo.findFirstByNombreUsuarioIgnoreCaseAndEstadoTrue(username)
                .orElseThrow(() -> new RuntimeException("No se pudo identificar el usuario autenticado"));

        return acceso.getUsuario().getIdUsuario();
    }

    @Transactional(readOnly = true)
    public List<HistorialSolicitudDto> historialSolicitud(Integer idSolicitud) {
        return solicitudGiraRepo.findHistorialSolicitud(idSolicitud).stream()
                .map(h -> new HistorialSolicitudDto(
                        h.getIdHistorial(),
                        h.getIdSolicitud(),
                        h.getIdPersona(),
                        h.getPersona(),
                        h.getAccion(),
                        h.getObservacion(),
                        h.getFecha()
                ))
                .toList();
    }


    @Transactional(readOnly = true)
    public List<UsuarioLiteDto> listarDocentes() {
        return usuarioRepo.findDocentesActivos().stream()
                .map(u -> new UsuarioLiteDto(u.getIdUsuario(), u.getNombres(), u.getApellidos(), u.getCorreo()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AsignaturaLiteDto> listarAsignaturas() {
        return asignaturaRepo.findAll().stream()
                .map(a -> new AsignaturaLiteDto(a.getIdasignatura(), a.getNombreasignatura(), a.getCreditos()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PeriodoLiteDto> listarPeriodos() {
        return periodoRepo.findAll().stream()
                .map(p -> new PeriodoLiteDto(p.getIdperiodo(), p.getPeriodo(), p.getFechaInicio(), p.getFechaFin(), p.getEstado()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AsignaturaLiteDto> listarAsignaturasAsignadas(Long docenteId, Integer periodoId) {
        return docenteAsignaturaRepo.findByDocenteIdAndPeriodoId(docenteId, periodoId).stream()
                .map(da -> da.getAsignatura())
                .map(a -> new AsignaturaLiteDto(a.getIdasignatura(), a.getNombreasignatura(), a.getCreditos()))
                .toList();
    }




}
