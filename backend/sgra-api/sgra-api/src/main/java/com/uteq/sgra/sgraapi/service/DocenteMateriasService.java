package com.uteq.sgra.sgraapi.service;

import com.uteq.sgra.sgraapi.dto.MateriaAsignadaDto;
import com.uteq.sgra.sgraapi.repo.DocenteAsignaturaRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocenteMateriasService {

    private final DocenteAsignaturaRepo repo;

    public DocenteMateriasService(DocenteAsignaturaRepo repo) {
        this.repo = repo;
    }

    public List<MateriaAsignadaDto> misMaterias(Long docenteId) {
        return repo.findAllByDocenteId(docenteId).stream()
                .map(da -> new MateriaAsignadaDto(
                        da.getAsignatura().getIdasignatura(),
                        da.getAsignatura().getNombre(),
                        da.getAsignatura().getCreditos(),
                        da.getPeriodo().getIdperiodo(),
                        da.getPeriodo().getPeriodo()
                ))
                .toList();
    }
}
