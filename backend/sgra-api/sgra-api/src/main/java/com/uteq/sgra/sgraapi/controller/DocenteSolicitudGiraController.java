package com.uteq.sgra.sgraapi.controller;

import com.uteq.sgra.sgraapi.dto.SolicitudGiraCreateRequest;
import com.uteq.sgra.sgraapi.dto.SolicitudGiraDto;
import com.uteq.sgra.sgraapi.entity.DocumentoSolicitud;
import com.uteq.sgra.sgraapi.service.DocenteSolicitudGiraService;
import com.uteq.sgra.sgraapi.service.DocumentoSolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/docente/solicitudes", "/api/docente/solicitud-gira"})
@RequiredArgsConstructor
public class DocenteSolicitudGiraController {

    private final DocenteSolicitudGiraService service;
    private final DocumentoSolicitudService documentoService;

    @PostMapping
    public ResponseEntity<SolicitudGiraDto> crear(
            Authentication auth,
            @Valid @RequestBody SolicitudGiraCreateRequest req
    ) {
        return ResponseEntity.ok(service.crear(auth.getName(), req));
    }

    @GetMapping("/mis-solicitudes")
    public ResponseEntity<List<SolicitudGiraDto>> listarMisSolicitudes(Authentication auth) {
        return ResponseEntity.ok(service.listarMisSolicitudes(auth.getName()));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> stats(Authentication auth) {
        return ResponseEntity.ok(service.obtenerStats(auth.getName()));
    }

    // ✅ NUEVO: crear solicitud + subir PDF en el mismo request
    @PostMapping(value = "/con-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crearConPdf(
            Authentication auth,
            @RequestParam Integer idAsignatura,
            @RequestParam Integer idEmpresa,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam String semana,
            @RequestParam(name = "cupo_Maximo") Integer cupoMaximo,
            @RequestParam(required = false) String tipoDocumento,
            @RequestPart(name = "file", required = false) MultipartFile file
    ) {
        SolicitudGiraCreateRequest req = new SolicitudGiraCreateRequest(
                idAsignatura, idEmpresa, fechaInicio, fechaFin, semana, cupoMaximo
        );

        SolicitudGiraDto solicitud = service.crear(auth.getName(), req);

        DocumentoSolicitud doc = null;
        if (file != null && !file.isEmpty()) {
            // ✅ OJO: tu DTO tiene id() NO idSolicitud()
            doc = documentoService.guardarPdf(solicitud.id(), tipoDocumento, file);
        }

        return ResponseEntity.ok(Map.of(
                "solicitud", solicitud,
                "documento", doc
        ));
    }
}