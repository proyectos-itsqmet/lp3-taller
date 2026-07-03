package com.code_hub.dto;

import java.time.LocalDate;
import java.util.List;

public record EmpleadoResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        String documento,
        LocalDate fechaContratacion,
        PerfilResponse perfil,
        List<ProyectoResumenResponse> proyectos
) {
}
