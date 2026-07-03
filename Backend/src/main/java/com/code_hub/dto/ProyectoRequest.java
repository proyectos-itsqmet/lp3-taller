package com.code_hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record ProyectoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String nombre,

        String descripcion,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        LocalDate fechaFin,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no puede superar 30 caracteres")
        String estado,

        @NotNull(message = "El presupuesto es obligatorio")
        @PositiveOrZero(message = "El presupuesto debe ser cero o positivo")
        BigDecimal presupuesto,

        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        Set<Long> empleadoIds
) {
}
