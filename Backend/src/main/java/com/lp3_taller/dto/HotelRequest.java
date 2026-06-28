package com.lp3_taller.dto;

import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HotelRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String nombre,

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
        String ciudad,

        @NotNull(message = "La categoria es obligatoria")
        @Min(value = 1, message = "La categoria minima es 1")
        @Max(value = 5, message = "La categoria maxima es 5")
        Integer categoria,

        @Size(max = 30, message = "El telefono no puede superar los 30 caracteres")
        String telefono,

        String imagen,

        Set<Long> servicioIds
) {
}
