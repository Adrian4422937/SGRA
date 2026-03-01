package com.uteq.sgra.sgraapi.repo;

import com.uteq.sgra.sgraapi.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolRepo extends JpaRepository<Rol, Long> {
    Optional<Rol> findByRolIgnoreCase(String rol);
    List<Rol> findByEstadoTrueOrderByRolAsc();
}
