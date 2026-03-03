package com.uteq.sgra.sgraapi.repo;

import com.uteq.sgra.sgraapi.entity.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRolRepo extends JpaRepository<UsuarioRol, Long> {

    // ✅ Devuelve los nombres de roles activos del usuario
    @Query("""
        select r.rol
        from UsuarioRol ur
        join ur.rol r
        where ur.usuario.idUsuario = :userId
          and ur.estado = true
          and r.estado = true
    """)
    List<String> findActiveRoleNamesByUsuarioId(@Param("userId") Long userId);

    // ✅ Desactiva roles del usuario
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update UsuarioRol ur
           set ur.estado = false
         where ur.usuario.idUsuario = :userId
    """)
    int disableByUsuarioId(@Param("userId") Long userId);

    Optional<UsuarioRol> findFirstByUsuario_IdUsuarioAndRol_RolIgnoreCase(Long userId, String rol);
}