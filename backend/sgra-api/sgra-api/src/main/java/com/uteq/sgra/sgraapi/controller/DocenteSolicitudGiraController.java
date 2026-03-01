package com.uteq.sgra.sgraapi.controller;

import com.uteq.sgra.sgraapi.dto.SolicitudGiraCreateRequest;
import com.uteq.sgra.sgraapi.dto.SolicitudGiraDto;
import com.uteq.sgra.sgraapi.service.DocenteSolicitudGiraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/docente/solicitudes", "/api/docente/solicitud-gira"})
@RequiredArgsConstructor
public class DocenteSolicitudGiraController {

    private final DocenteSolicitudGiraService service;

    @PostMapping
    public ResponseEntity<SolicitudGiraDto> crear(
            Authentication auth,
            @Valid @RequestBody SolicitudGiraCreateRequest req
    ) {
        return ResponseEntity.ok(service.crear(auth.getName(), req));
    }

    // ✅ NUEVO: lista de solicitudes del docente (con estado real)
    @GetMapping("/mis-solicitudes")
    public ResponseEntity<List<SolicitudGiraDto>> listarMisSolicitudes(Authentication auth) {
        return ResponseEntity.ok(service.listarMisSolicitudes(auth.getName()));
    }

    // ✅ NUEVO: stats reales
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> stats(Authentication auth) {
        return ResponseEntity.ok(service.obtenerStats(auth.getName()));
    }
}