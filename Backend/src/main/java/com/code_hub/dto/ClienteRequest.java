package com.code_hub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String nombre,

        @NotBlank(message = "El ruc es obligatorio")
        @Size(max = 20, message = "El ruc no puede superar 20 caracteres")
        String ruc,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es valido")
        @Size(max = 150, message = "El email no puede superar 150 caracteres")
        String email,

        @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
        String telefono,

        @Size(max = 200, message = "La direccion no puede superar 200 caracteres")
        String direccion
) {
}
