package com.lp3_taller.dto;

import java.math.BigDecimal;

public record HabitacionResponse(
        Long id,
        Long hotelId,
        String hotelNombre,
        String numero,
        String tipo,
        BigDecimal precioPorNoche,
        Integer capacidad,
        Boolean disponible,
        String imagen
) {
}
