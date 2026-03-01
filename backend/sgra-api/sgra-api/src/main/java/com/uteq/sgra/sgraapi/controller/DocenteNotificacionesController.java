package com.uteq.sgra.sgraapi.controller;

import com.uteq.sgra.sgraapi.dto.NotificacionDto;
import com.uteq.sgra.sgraapi.service.DocenteNotificacionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docente/notificaciones")
@RequiredArgsConstructor
public class DocenteNotificacionesController {

    private final DocenteNotificacionesService service;

    @GetMapping
    public ResponseEntity<List<NotificacionDto>> listar(
            Authentication auth,
            @RequestParam(required = false) Boolean noLeidas
    ) {
        return ResponseEntity.ok(service.listar(auth.getName(), noLeidas));
    }

    @PutMapping("/marcar-leidas")
    public ResponseEntity<Map<String, Integer>> marcarLeidas(Authentication auth) {
        int updated = service.marcarTodasLeidas(auth.getName());
        return ResponseEntity.ok(Map.of("updated", updated));
    }

    @GetMapping("/contador-no-leidas")
    public ResponseEntity<Map<String, Integer>> contarNoLeidas(Authentication auth) {
        int total = service.contarNoLeidas(auth.getName());
        return ResponseEntity.ok(Map.of("noLeidas", total));
    }
}
