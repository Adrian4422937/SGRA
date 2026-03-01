package com.uteq.sgra.sgraapi.repo;

import com.uteq.sgra.sgraapi.entity.SolicitudGira;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;



public interface SolicitudGiraRepo extends JpaRepository<SolicitudGira, Integer> {

    List<SolicitudGira> findByIdDocenteAndIdAsignaturaOrderByIdDesc(Long idDocente, Integer idAsignatura);

    List<SolicitudGira> findByIdDocenteAndEstadoActualIgnoreCaseOrderByIdDesc(Long idDocente, String estadoActual);

    List<SolicitudGira> findByIdDocenteAndIdAsignaturaAndEstadoActualIgnoreCaseOrderByIdDesc(
            Long idDocente, Integer idAsignatura, String estadoActual
    );
    List<SolicitudGira> findByIdDocenteOrderByIdDesc(Long idDocente);
    long countByIdDocente(Long idDocente);
    long countByIdDocenteAndEstadoActualContainingIgnoreCase(Long idDocente, String estado);
    // ===== Conteos dashboard =====
    long countByEstadoActualIgnoreCase(String estadoActual);

    // ===== Proyecciones para bandeja =====
    interface CoordinadorSolicitudRow {
        Integer getIdSolicitud();
        String getDocente();
        String getAsignatura();
        String getEmpresa();
        LocalDate getFechaSolicitud();
        LocalDate getFechaInicio();
        LocalDate getFechaFin();
        String getSemana();
        Integer getCupoMaximo();
        String getEstadoActual();
    }

    @Query(value = """
        SELECT
            s.idsolicitud AS idSolicitud,
            TRIM(COALESCE(u.nombres,'') || ' ' || COALESCE(u.apellidos,'')) AS docente,
            a.nombreasignatura AS asignatura,
            e.nombreempresa AS empresa,
            s.fecha_solicitud AS fechaSolicitud,
            s.fecha_inicio AS fechaInicio,
            s.fecha_fin AS fechaFin,
            s.semana AS semana,
            s.cupo_maximo AS cupoMaximo,
            s.estado_actual AS estadoActual
        FROM tbsolicitud_gira s
        JOIN tbusuarios u ON u.idusuario = s.iddocente
        JOIN tbasignatura a ON a.idasignatura = s.idasignatura
        JOIN tbempresa e ON e.idempresa = s.idempresa
        WHERE (:estado IS NULL OR :estado = '' OR UPPER(s.estado_actual) = UPPER(:estado))
        ORDER BY s.fecha_solicitud DESC, s.idsolicitud DESC
        """, nativeQuery = true)
    List<CoordinadorSolicitudRow> findBandejaCoordinador(@Param("estado") String estado);

    // ===== Proyección detalle =====
    interface CoordinadorSolicitudDetalleRow {
        Integer getIdSolicitud();
        Long getIdDocente();
        String getDocente();
        Integer getIdAsignatura();
        String getAsignatura();
        Integer getIdEmpresa();
        String getEmpresa();
        LocalDate getFechaSolicitud();
        LocalDate getFechaInicio();
        LocalDate getFechaFin();
        String getSemana();
        Integer getCupoMaximo();
        String getEstadoActual();
    }

    @Query(value = """
        SELECT
        s.idsolicitud AS idSolicitud,
        s.iddocente AS idDocente,
        TRIM(COALESCE(u.nombres,'') || ' ' || COALESCE(u.apellidos,'')) AS docente,
        s.idasignatura AS idAsignatura,
        a.nombreasignatura AS asignatura,
        s.idempresa AS idEmpresa,
        e.nombreempresa AS empresa,
        s.fecha_solicitud AS fechaSolicitud,
        s.fecha_inicio AS fechaInicio,
        s.fecha_fin AS fechaFin,
        s.semana AS semana,
        s.cupo_maximo AS cupoMaximo,
        s.estado_actual AS estadoActual
    FROM tbsolicitud_gira s
    JOIN tbusuarios u ON u.idusuario = s.iddocente
    JOIN tbasignatura a ON a.idasignatura = s.idasignatura
    JOIN tbempresa e ON e.idempresa = s.idempresa
    WHERE s.idsolicitud = :idSolicitud
    """, nativeQuery = true)
    Optional<CoordinadorSolicitudDetalleRow> findDetalleCoordinador(@Param("idSolicitud") Integer idSolicitud);

    // ===== Actualizar estado =====
    @Modifying
    @Query("UPDATE SolicitudGira s SET s.estadoActual = :estado WHERE s.id = :id")
    int actualizarEstado(@Param("id") Integer id, @Param("estado") String estado);

    // ===== Historial =====
    @Modifying
    @Query(value = """
        INSERT INTO tbhistorial_solicitud (idsolicitud, idpersona, accion, observacion, fecha)
        VALUES (:idSolicitud, :idPersona, :accion, :observacion, CURRENT_TIMESTAMP)
        """, nativeQuery = true)
    int insertarHistorial(
            @Param("idSolicitud") Integer idSolicitud,
            @Param("idPersona") Long idPersona,
            @Param("accion") String accion,
            @Param("observacion") String observacion
    );

    // ===== Notificación al docente =====
    @Modifying
    @Query(value = """
        INSERT INTO tbnotificacion (idusuario_destino, tipo, mensaje, leido, fecha)
        VALUES (:idUsuarioDestino, :tipo, :mensaje, false, CURRENT_TIMESTAMP)
        """, nativeQuery = true)
    int insertarNotificacion(
            @Param("idUsuarioDestino") Long idUsuarioDestino,
            @Param("tipo") String tipo,
            @Param("mensaje") String mensaje
    );
    @Query("""
        SELECT s
        FROM SolicitudGira s
        WHERE s.idDocente = :idDocente
          AND UPPER(s.estadoActual) IN :estados
        ORDER BY s.id DESC
    """)
    List<SolicitudGira> findMisGirasPorEstados(@Param("idDocente") Long idDocente,
                                               @Param("estados") List<String> estados);
}
