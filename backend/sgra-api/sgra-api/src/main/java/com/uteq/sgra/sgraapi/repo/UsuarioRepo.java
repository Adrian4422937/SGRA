package com.uteq.sgra.sgraapi.repo;

import com.uteq.sgra.sgraapi.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {
    //Optional<Usuario> findFirstByNombreUsuarioIgnoreCaseOrCorreoIgnoreCase(String nombreUsuario, String correo);
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByIdentificacion(String identificacion);
}