package com.uteq.sgra.sgraapi.service;

import com.uteq.sgra.sgraapi.dto.NotificacionDto;
import com.uteq.sgra.sgraapi.repo.NotificacionJdbcRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocenteNotificacionesService {

    private final DocenteContext docenteContext;
    private final NotificacionJdbcRepo notificacionRepo;

    @Transactional(readOnly = true)
    public List<NotificacionDto> listar(String login, Boolean noLeidas) {
        Long idDocente = docenteContext.requireIdDocente(login);
        return notificacionRepo.listarPorDestino(idDocente, noLeidas);
    }

    @Transactional
    public int marcarTodasLeidas(String login) {
        Long idDocente = docenteContext.requireIdDocente(login);
        return notificacionRepo.marcarTodasLeidas(idDocente);
    }
    @Transactional(readOnly = true)
    public int contarNoLeidas(String login) {
        Long idDocente = docenteContext.requireIdDocente(login);
        return notificacionRepo.contarNoLeidas(idDocente);
    }
}
