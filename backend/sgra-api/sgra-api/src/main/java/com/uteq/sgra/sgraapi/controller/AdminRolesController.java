package com.uteq.sgra.sgraapi.controller;

import com.uteq.sgra.sgraapi.repo.RolRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRolesController {

    private final RolRepo rolRepo;

    @GetMapping
    public List<String> roles() {
        return rolRepo.findByEstadoTrueOrderByRolAsc()
                .stream()
                .map(r -> r.getRol().toUpperCase())
                .toList();
    }
}