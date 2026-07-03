package com.code_hub.dto;

import java.util.List;

public record ClienteResponse(
        Long id,
        String nombre,
        String ruc,
        String email,
        String telefono,
        String direccion,
        List<ProyectoResumenResponse> proyectos
) {
}
