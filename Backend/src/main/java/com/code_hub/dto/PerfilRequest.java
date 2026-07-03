package com.code_hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PerfilRequest(
        @NotBlank(message = "El cargo es obligatorio")
        @Size(max = 100, message = "El cargo no puede superar 100 caracteres")
        String cargo,

        @Size(max = 100, message = "La especialidad no puede superar 100 caracteres")
        String especialidad,

        @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
        String telefono,

        String bio,

        @PositiveOrZero(message = "El salario debe ser cero o positivo")
        BigDecimal salario
) {
}
