package com.uteq.sgra.sgraapi.service;

import com.uteq.sgra.sgraapi.entity.DocumentoSolicitud;
import com.uteq.sgra.sgraapi.repo.DocumentoSolicitudRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentoSolicitudService {

    private final DocumentoSolicitudRepo repo;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public DocumentoSolicitud guardarPdf(Integer idSolicitud, String tipoDocumento, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se envió ningún archivo");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "documento.pdf" : file.getOriginalFilename());
        String lower = originalName.toLowerCase();

        // ✅ Validación simple: solo PDF
        if (!lower.endsWith(".pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permite subir archivos .pdf");
        }

        // Carpeta: uploads/solicitudes/{idSolicitud}/
        Path dir = Paths.get(uploadDir, "solicitudes", String.valueOf(idSolicitud));
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo crear la carpeta de subida");
        }

        // Nombre final único
        String finalName = UUID.randomUUID() + "__" + originalName;
        Path dest = dir.resolve(finalName).normalize();

        try {
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el archivo");
        }

        // ruta relativa para guardar en BD
        String rutaRelativa = Paths.get("solicitudes", String.valueOf(idSolicitud), finalName).toString().replace("\\", "/");

        DocumentoSolicitud doc = repo.save(DocumentoSolicitud.builder()
                .idSolicitud(idSolicitud)
                .tipoDocumento((tipoDocumento == null || tipoDocumento.isBlank()) ? "PLAN_GIRA" : tipoDocumento)
                .archivo(rutaRelativa)
                .fechaSubida(LocalDateTime.now())
                .build());

        return doc;
    }

    public List<DocumentoSolicitud> listar(Integer idSolicitud) {
        return repo.findByIdSolicitudOrderByFechaSubidaDesc(idSolicitud);
    }

    public DocumentoSolicitud obtener(Integer idSolicitud, Integer idDocumento) {
        return repo.findByIdDocumentoAndIdSolicitud(idDocumento, idSolicitud)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento no encontrado"));
    }

    public Path resolveFilePath(String rutaRelativa) {
        return Paths.get(uploadDir).resolve(rutaRelativa).normalize();
    }
}