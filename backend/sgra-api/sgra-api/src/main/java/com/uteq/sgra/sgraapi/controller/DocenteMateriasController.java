package com.uteq.sgra.sgraapi.controller;

import com.uteq.sgra.sgraapi.dto.MateriaAsignadaDto;
import com.uteq.sgra.sgraapi.entity.Acceso;
import com.uteq.sgra.sgraapi.repo.AccesoRepo;
import com.uteq.sgra.sgraapi.service.DocenteMateriasService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/docente")
public class DocenteMateriasController {

    private final AccesoRepo accesoRepo;
    private final DocenteMateriasService service;

    public DocenteMateriasController(AccesoRepo accesoRepo, DocenteMateriasService service) {
        this.accesoRepo = accesoRepo;
        this.service = service;
    }

    @GetMapping("/mis-materias")
    public List<MateriaAsignadaDto> misMaterias(Authentication auth) {
        String login = auth.getName(); // sale del JWT (puede ser username o correo)

        Acceso acceso = accesoRepo.findFirstByNombreUsuarioIgnoreCaseAndEstadoTrue(login)
                .or(() -> accesoRepo.findFirstByUsuario_CorreoIgnoreCaseAndEstadoTrue(login))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Acceso no encontrado para: " + login
                ));

        Long idDocente = acceso.getUsuario().getIdUsuario();
        return service.misMaterias(idDocente);
    }
}
