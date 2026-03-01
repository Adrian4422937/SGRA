package com.uteq.sgra.sgraapi.repo;

import com.uteq.sgra.sgraapi.dto.NotificacionDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class NotificacionJdbcRepo {

    private final JdbcTemplate jdbc;

    public NotificacionJdbcRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<NotificacionDto> listarPorDestino(Long idUsuarioDestino, Boolean noLeidas) {
        String sql = """
            SELECT tipo, mensaje, leido, fecha
            FROM tbnotificacion
            WHERE idusuario_destino = ?
              AND (COALESCE(?, false) = false OR leido = false)
            ORDER BY fecha DESC
            LIMIT 100
        """;

        return jdbc.query(sql,
                new Object[]{idUsuarioDestino, noLeidas},
                (rs, rowNum) -> {
                    Timestamp ts = rs.getTimestamp("fecha");
                    return new NotificacionDto(
                            rs.getString("tipo"),
                            rs.getString("mensaje"),
                            rs.getBoolean("leido"),
                            ts != null ? ts.toLocalDateTime() : null
                    );
                }
        );
    }

    public int marcarTodasLeidas(Long idUsuarioDestino) {
        String sql = """
            UPDATE tbnotificacion
            SET leido = true
            WHERE idusuario_destino = ?
              AND leido = false
        """;
        return jdbc.update(sql, idUsuarioDestino);
    }

    public int contarNoLeidas(Long idUsuarioDestino) {
        String sql = """
            SELECT COUNT(*)
            FROM tbnotificacion
            WHERE idusuario_destino = ?
              AND leido = false
        """;

        Long total = jdbc.queryForObject(sql, Long.class, idUsuarioDestino);
        return total != null ? total.intValue() : 0;
    }
}