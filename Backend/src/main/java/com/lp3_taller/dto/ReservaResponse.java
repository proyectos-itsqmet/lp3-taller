package com.lp3_taller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservaResponse(
        Long id,
        Long usuarioId,
        String usuarioNombre,
        Long habitacionId,
        String habitacionNumero,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        String estado,
        BigDecimal total
) {
}
