package com.uteq.sgra.sgraapi.repo;

import com.uteq.sgra.sgraapi.entity.PeriodoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PeriodoAcademicoRepo extends JpaRepository<PeriodoAcademico, Integer> {
    Optional<PeriodoAcademico> findFirstByEstadoIgnoreCase(String estado);
}
