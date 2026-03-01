package com.uteq.sgra.sgraapi.controller;

import com.uteq.sgra.sgraapi.dto.SolicitudGiraDto;
import com.uteq.sgra.sgraapi.service.DocenteEstadoSolicitudesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docente/estado-solicitudes")
@RequiredArgsConstructor
public class DocenteEstadoSolicitudesController {

    private final DocenteEstadoSolicitudesService service;

    @GetMapping
    public ResponseEntity<List<SolicitudGiraDto>> listar(
            Authentication auth,
            @RequestParam(required = false) Integer idAsignatura,
            @RequestParam(required = false) String estado
    ) {
        return ResponseEntity.ok(service.listar(auth.getName(), idAsignatura, estado));
    }
}
