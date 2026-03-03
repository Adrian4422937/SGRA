package com.uteq.sgra.sgraapi.dto;

public record UsuarioLiteDto(
        Long id,
        String nombres,
        String apellidos,
        String correo
) {}
