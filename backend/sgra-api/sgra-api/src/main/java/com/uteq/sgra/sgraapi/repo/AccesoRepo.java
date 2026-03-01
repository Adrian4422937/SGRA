package com.uteq.sgra.sgraapi.repo;

import com.uteq.sgra.sgraapi.entity.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AccesoRepo extends JpaRepository<Acceso, Long> {

    Optional<Acceso> findByNombreUsuarioIgnoreCase(String nombreUsuario);
    boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);

    Optional<Acceso> findFirstByUsuario_IdUsuario(Long idUsuario);
    Optional<Acceso> findFirstByUsuario_IdUsuarioAndEstadoTrue(Long idUsuario);

    Optional<Acceso> findFirstByNombreUsuarioIgnoreCaseAndEstadoTrue(String nombreUsuario);
    Optional<Acceso> findFirstByUsuario_CorreoIgnoreCaseAndEstadoTrue(String correo);
    void deleteByUsuario_IdUsuario(Long idUsuario);

    @Transactional
    @Modifying
    @Query("update Acceso a set a.estado = false where a.usuario.idUsuario = :userId")
    int disableByUsuarioId(@Param("userId") Long userId);
}