package com.lp3_taller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoResponse(
        Long id,
        Long reservaId,
        BigDecimal monto,
        LocalDate fecha,
        String metodo
) {
}
