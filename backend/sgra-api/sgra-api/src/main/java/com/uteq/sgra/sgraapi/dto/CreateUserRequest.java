package com.uteq.sgra.sgraapi.dto;

import java.util.Set;

public record CreateUserRequest(
        // TbUsuarios (obligatorios)
        String nombres,
        String apellidos,
        String identificacion,
        String correo,
        String perfilUsuario,

        // TbUsuarios (opcionales)
        String telefono,
        Long idGenero,

        // TbAccesos (obligatorios)
        String nombreUsuario,
        String password,

        // roles
        Set<String> roles
) {}