package com.code_hub.dto;

public record UsuarioResponse(
        Long id,
        String username,
        String nombre,
        String email,
        Boolean enabled,
        String rol
) {
}
