package com.code_hub.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmpleadoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
        String apellido,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es valido")
        @Size(max = 150, message = "El email no puede superar 150 caracteres")
        String email,

        @NotBlank(message = "El documento es obligatorio")
        @Size(max = 30, message = "El documento no puede superar 30 caracteres")
        String documento,

        @NotNull(message = "La fecha de contratacion es obligatoria")
        LocalDate fechaContratacion,

        @NotNull(message = "El perfil es obligatorio")
        @Valid
        PerfilRequest perfil
) {
}
