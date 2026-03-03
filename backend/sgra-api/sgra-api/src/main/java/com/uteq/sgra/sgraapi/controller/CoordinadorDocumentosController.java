package com.uteq.sgra.sgraapi.controller;

import com.uteq.sgra.sgraapi.service.DocumentoSolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/coordinador/solicitudes")
@RequiredArgsConstructor
public class CoordinadorDocumentosController {

    private final DocumentoSolicitudService documentoService;

    @GetMapping("/{idSolicitud}/documentos")
    public ResponseEntity<?> listar(@PathVariable Integer idSolicitud) {
        return ResponseEntity.ok(documentoService.listar(idSolicitud));
    }

    @GetMapping("/{idSolicitud}/documentos/{idDocumento}/download")
    public ResponseEntity<Resource> download(
            @PathVariable Integer idSolicitud,
            @PathVariable Integer idDocumento
    ) throws Exception {

        var doc = documentoService.obtener(idSolicitud, idDocumento);
        Path path = documentoService.resolveFilePath(doc.getArchivo());

        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName().toString() + "\"")
                .body(resource);
    }
}
