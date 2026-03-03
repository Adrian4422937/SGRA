package com.uteq.sgra.sgraapi.repo;

import com.uteq.sgra.sgraapi.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {

    @Query(value = """
        SELECT u.*
        FROM tbusuarios u
        JOIN tbusuariosroles ur ON ur.idusuario = u.idusuario
        JOIN tbroles r ON r.idrol = ur.idrol
        WHERE ur.estado = true
          AND r.rol = 'ROLE_DOCENTE'
        ORDER BY u.nombres, u.apellidos
    """, nativeQuery = true)
    List<Usuario> findDocentesActivos();

    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
        FROM tbusuariosroles ur
        JOIN tbroles r ON r.idrol = ur.idrol
        WHERE ur.estado = true
          AND ur.idusuario = :userId
          AND r.rol = 'ROLE_DOCENTE'
    """, nativeQuery = true)
    boolean isDocente(@Param("userId") Long userId);


    //Optional<Usuario> findFirstByNombreUsuarioIgnoreCaseOrCorreoIgnoreCase(String nombreUsuario, String correo);
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByIdentificacion(String identificacion);
}