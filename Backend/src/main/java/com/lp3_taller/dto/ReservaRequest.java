package com.lp3_taller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReservaRequest(

        @NotNull(message = "El usuarioId es obligatorio")
        Long usuarioId,

        @NotNull(message = "El habitacionId es obligatorio")
        Long habitacionId,

        @NotNull(message = "La fecha de entrada es obligatoria")
        LocalDate fechaEntrada,

        @NotNull(message = "La fecha de salida es obligatoria")
        LocalDate fechaSalida,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no puede superar los 30 caracteres")
        String estado,

        @NotNull(message = "El total es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El total debe ser mayor a 0")
        BigDecimal total
) {
}
