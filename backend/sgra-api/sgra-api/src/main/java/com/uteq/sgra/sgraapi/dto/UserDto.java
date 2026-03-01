package com.uteq.sgra.sgraapi.dto;

import java.util.Set;

public record UserDto(
        Long id,
        String nombreUsuario,
        String nombres,
        String apellidos,
        String correo,
        String perfilUsuario,
        Set<String> roles
) {}
