package com.code_hub.dto;

public record AuthResponse(
        Long id,
        String username,
        String nombre,
        String email,
        String rol
) {
}
