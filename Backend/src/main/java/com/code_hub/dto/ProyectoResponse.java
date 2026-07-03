package com.code_hub.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProyectoResponse(
        Long id,
        String nombre,
        String descripcion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado,
        BigDecimal presupuesto,
        Long clienteId,
        String clienteNombre,
        List<EmpleadoResumenResponse> empleados
) {
}
