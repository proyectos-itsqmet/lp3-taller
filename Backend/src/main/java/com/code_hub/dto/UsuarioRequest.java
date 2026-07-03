package com.code_hub.dto;

import com.code_hub.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "El username es obligatorio")
        @Size(max = 60, message = "El username no puede superar 60 caracteres")
        String username,

        @NotBlank(message = "El password es obligatorio")
        @Size(min = 4, max = 100, message = "El password debe tener entre 4 y 100 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es valido")
        @Size(max = 150, message = "El email no puede superar 150 caracteres")
        String email,

        @NotNull(message = "El rol es obligatorio")
        Rol rol,

        @NotNull(message = "El estado enabled es obligatorio")
        Boolean enabled
) {
}
