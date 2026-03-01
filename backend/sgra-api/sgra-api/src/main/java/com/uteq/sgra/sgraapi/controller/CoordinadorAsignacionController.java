package com.uteq.sgra.sgraapi.controller;

import com.uteq.sgra.sgraapi.dto.*;
import com.uteq.sgra.sgraapi.service.CoordinadorAsignacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordinador")
public class CoordinadorAsignacionController {

    private final CoordinadorAsignacionService service;

    public CoordinadorAsignacionController(CoordinadorAsignacionService service) {
        this.service = service;
    }

    // =========================
    // YA TENÍAS ESTO (se mantiene)
    // =========================
    @PostMapping("/asignar-materias")
    public ResponseEntity<?> asignarMaterias(@RequestBody AsignarMateriasRequest req) {
        service.asignar(req);
        return ResponseEntity.ok().build();
    }

    // =========================
    // NUEVO: dashboard
    // =========================
    @GetMapping("/dashboard/resumen")
    public ResponseEntity<CoordinadorDashboardResumenDto> resumenDashboard() {
        return ResponseEntity.ok(service.resumenDashboard());
    }

    // =========================
    // NUEVO: bandeja solicitudes
    // =========================
    @GetMapping("/solicitudes")
    public ResponseEntity<List<CoordinadorSolicitudItemDto>> listarSolicitudes(
            @RequestParam(required = false) String estado
    ) {
        return ResponseEntity.ok(service.listarSolicitudes(estado));
    }

    // =========================
    // NUEVO: detalle
    // =========================
    @GetMapping("/solicitudes/{id}")
    public ResponseEntity<CoordinadorSolicitudDetalleDto> detalleSolicitud(@PathVariable Integer id) {
        return ResponseEntity.ok(service.detalleSolicitud(id));
    }

    // =========================
    // NUEVO: aprobar
    // =========================
    @PutMapping("/solicitudes/{id}/aprobar")
    public ResponseEntity<?> aprobarSolicitud(
            @PathVariable Integer id,
            @RequestBody(required = false) CambiarEstadoSolicitudRequest req
    ) {
        service.aprobarSolicitud(id, req != null ? req.observacion() : null);
        return ResponseEntity.ok().build();
    }

    // =========================
    // NUEVO: rechazar
    // =========================
    @PutMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(
            @PathVariable Integer id,
            @RequestBody(required = false) CambiarEstadoSolicitudRequest req
    ) {
        service.rechazarSolicitud(id, req != null ? req.observacion() : null);
        return ResponseEntity.ok().build();
    }
}