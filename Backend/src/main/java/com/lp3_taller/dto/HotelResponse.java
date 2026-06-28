package com.lp3_taller.dto;

import java.util.List;

public record HotelResponse(
        Long id,
        String nombre,
        String ciudad,
        Integer categoria,
        String telefono,
        String imagen,
        List<ServicioResponse> servicios
) {
}
