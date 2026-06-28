package com.lp3_taller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PagoRequest(

        @NotNull(message = "El reservaId es obligatorio")
        Long reservaId,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
        BigDecimal monto,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotBlank(message = "El metodo es obligatorio")
        @Size(max = 50, message = "El metodo no puede superar los 50 caracteres")
        String metodo
) {
}
