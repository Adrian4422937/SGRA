package com.uteq.sgra.sgraapi.service;

import com.uteq.sgra.sgraapi.repo.AccesoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class DocenteContext {

    private final AccesoRepo accesoRepo;

    public Long requireIdDocente(String username) {
        var acceso = accesoRepo.findFirstByNombreUsuarioIgnoreCaseAndEstadoTrue(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Acceso inválido"));
        return acceso.getUsuario().getIdUsuario(); // según tu modelo actual
    }
}
