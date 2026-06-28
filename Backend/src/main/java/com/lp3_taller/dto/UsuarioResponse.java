package com.lp3_taller.dto;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String documento
) {
}
