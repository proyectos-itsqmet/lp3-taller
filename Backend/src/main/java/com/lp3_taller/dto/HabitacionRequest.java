package com.lp3_taller.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record HabitacionRequest(

        @NotNull(message = "El hotelId es obligatorio")
        Long hotelId,

        @NotBlank(message = "El numero es obligatorio")
        @Size(max = 20, message = "El numero no puede superar los 20 caracteres")
        String numero,

        @NotBlank(message = "El tipo es obligatorio")
        @Size(max = 50, message = "El tipo no puede superar los 50 caracteres")
        String tipo,

        @NotNull(message = "El precio por noche es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio por noche debe ser mayor a 0")
        BigDecimal precioPorNoche,

        @NotNull(message = "La capacidad es obligatoria")
        @Positive(message = "La capacidad debe ser mayor a 0")
        Integer capacidad,

        @NotNull(message = "La disponibilidad es obligatoria")
        Boolean disponible,

        String imagen
) {
}
